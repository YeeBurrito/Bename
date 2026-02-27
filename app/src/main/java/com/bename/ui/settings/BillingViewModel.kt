package com.bename.ui.settings

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.bename.data.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    app: Application,
    private val settingsDataStore: SettingsDataStore
) : AndroidViewModel(app), PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingViewModel"
        private const val PRODUCT_PREMIUM = "premium"
    }

    private val _premiumPurchased = MutableStateFlow(false)
    val premiumPurchased: StateFlow<Boolean> = _premiumPurchased
    private val _isPremiumProductReady = MutableStateFlow(false)
    val isPremiumProductReady: StateFlow<Boolean> = _isPremiumProductReady

    private var premiumProductDetails: ProductDetails? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(app)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    init {
        startBillingConnection()
    }

    private fun startBillingConnection() {
        Log.d(TAG, "Starting billing connection")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(TAG, "Billing setup finished with response code: ${billingResult.responseCode}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    checkExistingPurchases()
                }

            }
            override fun onBillingServiceDisconnected() {
                // Optionally handle reconnection
            }
        })
    }

    private fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_PREMIUM)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            val details = productDetailsList.productDetailsList
            Log.d(TAG, "Product details query result: ${billingResult.responseCode}, found ${details.size} products")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && details.isNotEmpty()) {
                for (product in details) {
                    Log.d(TAG, "Found product: ${product.productId}")
                    if (product.productId == PRODUCT_PREMIUM) {
                        premiumProductDetails = product
                        _isPremiumProductReady.value = true
                        break
                    }
                }
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity) {
        val productDetails = premiumProductDetails ?: return
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: productDetails.oneTimePurchaseOfferDetails?.let { "" } // For INAPP, offerToken is not needed

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            checkExistingPurchases()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "User canceled purchase")
        } else {
            Log.d(TAG, "Purchase failed: ${billingResult.responseCode}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(PRODUCT_PREMIUM)) {
            viewModelScope.launch {
                settingsDataStore.setPremiumEnabled(true)
                _premiumPurchased.value = true
            }
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) {}
            }
        }
    }

    private fun checkExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            val hasPremium = purchasesList.any { it.products.contains(PRODUCT_PREMIUM) }
            _premiumPurchased.value = hasPremium
            viewModelScope.launch {
                settingsDataStore.setPremiumEnabled(hasPremium)
            }
        }
    }
}