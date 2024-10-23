package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.presentation.coin_list.components.CoinListItem
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUi
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListScreen(
    state: CoinListState,
    modifier: Modifier = Modifier) {

    if(state.isLoading){
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier= modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.coins) { coinUi ->
                CoinListItem(
                    coinUi = coinUi,
                    onClick = { /* TODO */ }

                )
                HorizontalDivider()
            }

        }
    }
}

@PreviewLightDark
@Composable
private fun CoinListScreenPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            state = CoinListState(
                isLoading = false,
                coins = coinsPreview()
            ),
            modifier = Modifier.background(
                MaterialTheme.colorScheme.background
            )
        )
    }
}

internal fun coinsPreview(): List<CoinUi> {
    val list = mutableListOf<CoinUi>()
    list.add(
        Coin(
        id = "ether",
        rank = 1,
        name = "Ether",
        symbol = "ETH",
        marketCapUsd = 123123.74,
        priceUsd = 900.45,
        changePercent24Hr = 0.9
    ).toCoinUi())
    list.add(
        Coin(
            id = "bitcoin",
            rank = 2,
            name = "Bitcoin",
            symbol = "BTC",
            marketCapUsd = 1234567891232.74,
            priceUsd = 62838.45,
            changePercent24Hr = 0.6
        ).toCoinUi())

    list.add(
        Coin(
            id = "solana",
            rank = 3,
            name = "Solana",
            symbol = "SOL",
            marketCapUsd = 78.74,
            priceUsd = 880.45,
            changePercent24Hr = -0.2
        ).toCoinUi())
    return list
}