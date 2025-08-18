package com.bename.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.bename.data.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.firstOrNull

@HiltViewModel
class BillingViewModel @Inject constructor(
    app: Application,
    private val settingsDataStore: SettingsDataStore
) : AndroidViewModel(app) {

    private val _premiumPurchased = MutableStateFlow(false)
    val premiumPurchased: StateFlow<Boolean> = _premiumPurchased

    private val billingClient = BillingClient.newBuilder(app)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private var premiumProductDetails: ProductDetails? = null

    init {
        connectBillingClient()
    }

    private fun connectBillingClient() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryPurchases()
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    fun queryProductDetails() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("premium") // Replace with your actual product ID
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle fetched product details
                for (productDetails in productDetailsResult.productDetailsList) {
                    premiumProductDetails = productDetails
                    // You can update your UI or state here
                }
//                // Handle unfetched products if needed
//                for (unfetchedProduct in productDetailsResult.unfetchedProducts) {
//                    // Handle unfetched products as appropriate
//                }
            } else {
                // Handle error
            }
        }
    }


    fun launchPurchaseFlow(activity: android.app.Activity) {
        val productDetails = premiumProductDetails ?: return
        val offerToken = productDetails.oneTimePurchaseOfferDetails?.offerToken ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, params)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            viewModelScope.launch {
                settingsDataStore.setPremiumEnabled(true)
                _premiumPurchased.value = true
            }
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) {}
        }
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchasesList ->
            val hasPremium = purchasesList.any { it.products.contains("premium_upgrade") }
            _premiumPurchased.value = hasPremium
            viewModelScope.launch {
                settingsDataStore.setPremiumEnabled(hasPremium)
            }
        }
    }
}