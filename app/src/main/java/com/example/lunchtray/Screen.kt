package com.example.lunchtray

import androidx.annotation.StringRes

enum class Screen(@StringRes val title: Int){
    START(title = R.string.app_name),
    ENTREE(title = R.string.choose_entree),
    SIDE(title = R.string.choose_side_dish),
    ACCOMPANIMENT(title = R.string.choose_accompaniment),
    CHECKOUT(title = R.string.order_checkout)
}