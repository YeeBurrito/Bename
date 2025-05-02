package com.example.bename

import androidx.navigation.NavHostController
import com.example.bename.BenameDestinationsArgs.PERSON_ID_ARG
import com.example.bename.BenameDestinationsArgs.TAG_ID_ARG
import com.example.bename.BenameDestinationsArgs.TAG_NAME_ARG
import com.example.bename.BenameDestinationsArgs.USER_MESSAGE_ARG
import com.example.bename.BenameScreens.ADD_EDIT_PERSON_SCREEN
import com.example.bename.BenameScreens.ADD_EDIT_TAG_SCREEN
import com.example.bename.BenameScreens.PEOPLE_SCREEN
import com.example.bename.BenameScreens.SEARCH_SCREEN
import com.example.bename.BenameScreens.SETTINGS_SCREEN
import com.example.bename.BenameScreens.TAGS_SCREEN

/**
 * Screens used in [BenameDestinations]
 */
private object BenameScreens {
    const val PEOPLE_SCREEN = "people"
    const val ADD_EDIT_PERSON_SCREEN = "addEditPerson"
    const val TAGS_SCREEN = "tags"
    const val ADD_EDIT_TAG_SCREEN = "addEditTag"
    const val SETTINGS_SCREEN = "settings"
    const val SEARCH_SCREEN = "search"
}

/**
 * Arguments used in [BenameDestinations] routes
 */
object BenameDestinationsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val PERSON_ID_ARG = "personId"
    const val TAG_ID_ARG = "tagId"
    const val TAG_NAME_ARG = "tagName"
}

/**
 * Destinations used in the [BenameActivity]
 */
object BenameDestinations {
    const val PEOPLE_ROUTE = "$PEOPLE_SCREEN?$USER_MESSAGE_ARG={$USER_MESSAGE_ARG}"
    const val ADD_EDIT_PERSON_ROUTE = "$ADD_EDIT_PERSON_SCREEN/{$PERSON_ID_ARG}"
    const val TAGS_ROUTE = "$TAGS_SCREEN?$USER_MESSAGE_ARG={$USER_MESSAGE_ARG}"
    const val ADD_EDIT_TAG_ROUTE = "$ADD_EDIT_TAG_SCREEN/{$TAG_ID_ARG}?$TAG_NAME_ARG={$TAG_NAME_ARG}"
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
    const val SEARCH_ROUTE = SEARCH_SCREEN
}

class BenameNavigationActions(private val navController: NavHostController) {

    fun navigateToPeople(personId: Long = 0L) {
        navController.navigate(
            PEOPLE_SCREEN.let {
                if (personId != 0L) "$it?$PERSON_ID_ARG=$personId" else it
            }
        )
    }

    fun navigateToAddEditPerson(personId: Long = 0L) {
        navController.navigate("$ADD_EDIT_PERSON_SCREEN/$personId")
    }

    fun navigateToTags(tagId: Long = 0L) {
        navController.navigate(
            TAGS_SCREEN.let {
                if (tagId != 0L) "$it?$TAG_ID_ARG=$tagId" else it
            }
        )
    }

    fun navigateToAddEditTag(tagId: Long = 0L) {
        navController.navigate("$ADD_EDIT_TAG_SCREEN/$tagId")
    }

    fun navigateToSettings() {
        navController.navigate(SETTINGS_SCREEN)
    }

    fun navigateToSearch() {
        navController.navigate(SEARCH_SCREEN)
    }
}

