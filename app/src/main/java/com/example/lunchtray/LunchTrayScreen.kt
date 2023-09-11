/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.LunchAppBar
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: AppBar

@Composable
fun LunchTrayApp(navController: NavHostController = rememberNavController()) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(backStackEntry?.destination?.route ?: Screen.START.name)

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            LunchAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = Screen.START.name,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = Screen.START.name){
                StartOrderScreen(
                    onStartOrderButtonClicked = { navController.navigate(Screen.ENTREE.name) },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)).fillMaxSize()
                )
            }

            composable(route = Screen.ENTREE.name){
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { cancelAndNavigateToStart(viewModel, navController) },
                    onNextButtonClicked = { navController.navigate(Screen.SIDE.name) },
                    onSelectionChanged = {entry -> viewModel.updateEntree(entry)}
                )
            }

            composable(route = Screen.SIDE.name){
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { navController.navigate(Screen.ENTREE.name) },
                    onNextButtonClicked = { navController.navigate(Screen.ACCOMPANIMENT.name) },
                    onSelectionChanged = {sideDish -> viewModel.updateSideDish(sideDish)}
                )
            }

            composable(route = Screen.ACCOMPANIMENT.name){
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { navController.navigate(Screen.SIDE.name) },
                    onNextButtonClicked = { navController.navigate(Screen.CHECKOUT.name) },
                    onSelectionChanged = {accompanimentItem -> viewModel.updateAccompaniment(accompanimentItem) }
                )
            }

            composable(route = Screen.CHECKOUT.name){
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = { cancelAndNavigateToStart(viewModel, navController) },
                    onCancelButtonClicked = { navController.navigate(Screen.ACCOMPANIMENT.name)})
            }
        }
    }
}

fun cancelAndNavigateToStart(viewModel: OrderViewModel, navController: NavHostController) {
    viewModel.resetOrder()
    navController.popBackStack(Screen.START.name, inclusive = false)
}
