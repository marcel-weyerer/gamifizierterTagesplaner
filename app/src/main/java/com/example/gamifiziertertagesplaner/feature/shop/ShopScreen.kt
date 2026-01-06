package com.example.gamifiziertertagesplaner.feature.shop

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.feature.bookshelf.bookSlots
import com.example.gamifiziertertagesplaner.feature.bookshelf.decorationSlots
import com.example.gamifiziertertagesplaner.feature.bookshelf.plantSlots
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.example.gamifiziertertagesplaner.ui.theme.bookPrice
import com.example.gamifiziertertagesplaner.ui.theme.decorationPrice
import com.example.gamifiziertertagesplaner.ui.theme.plantPrice

/**
 * In app shop screen
 */
@Composable
fun ShopScreen(
  authViewModel: AuthViewModel,
  onOpenHome: () -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenAchievements: () -> Unit,
) {
  val context = LocalContext.current

  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.home),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Home",
            onClick = onOpenHome
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.book),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Settings",
            onClick = onOpenBookshelf
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.shopping_cart),
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Pomodoro",
            onClick = {}
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.trophy),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Bücherregal",
            onClick = onOpenAchievements
          )
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Stats for the amount of each item in the basket
      var bookAmount by remember { mutableIntStateOf(0) }
      var plantAmount by remember { mutableIntStateOf(0) }
      var decotrationAmount by remember { mutableIntStateOf(0) }

      // Calculate the remaining amount of each item the user can buy
      val maxAvailableBooks = bookSlots.size - (authViewModel.userProfile?.boughtBooks ?: 0)
      val maxAvailablePlants = plantSlots.size - (authViewModel.userProfile?.boughtPlants ?: 0)
      val maxAvailableDecorations = decorationSlots.size - (authViewModel.userProfile?.boughtDecoration ?: 0)

      val userPoints = authViewModel.userProfile?.userPoints ?: 0

      TopScreenTitle(title = "Buchladen")

      // Score
      Text(
        text = "Punktestand",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Text(
        text = userPoints.toString(),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Book shop section
      ItemShopSection(
        item = "Buch",
        painterResource = painterResource(R.drawable.book_shop_icon),
        price = bookPrice,
        itemAmount = bookAmount,
        onIncreaseAmount = {
          // Only allow buying up to the maximum amount of books
          if (bookAmount < maxAvailableBooks) {
            bookAmount++
          } else {
            Toast.makeText(context, "Alle Bücher gekauft!", Toast.LENGTH_SHORT).show()
          }
        },
        onReduceAmount = { bookAmount = (bookAmount - 1).coerceIn(0, maxAvailableBooks) }
      )

      // Plant shop section
      ItemShopSection(
        item = "Pflanze",
        painterResource = painterResource(R.drawable.plant_shop_icon),
        price = plantPrice,
        itemAmount = plantAmount,
        onIncreaseAmount = {
          // Only allow buying up to the maximum amount of plants
          if (plantAmount < maxAvailablePlants) {
            plantAmount++
          } else {
            Toast.makeText(context, "Alle Pflanzen gekauft!", Toast.LENGTH_SHORT).show()
          }
        },
        onReduceAmount = { plantAmount = (plantAmount - 1).coerceIn(0, maxAvailablePlants) }
      )

      // Decoration shop section
      ItemShopSection(
        item = "Dekoration",
        painterResource = painterResource(R.drawable.decoration_shop_icon),
        price = decorationPrice,
        itemAmount = decotrationAmount,
        onIncreaseAmount = {
          // Only allow buying up to the maximum amount of decorations
          if (decotrationAmount < maxAvailableDecorations) {
            decotrationAmount++
          } else {
            Toast.makeText(context, "Alle Dekorationen gekauft!", Toast.LENGTH_SHORT).show()
          }
        },
        onReduceAmount = { decotrationAmount = (decotrationAmount - 1).coerceIn(0, maxAvailableDecorations) }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Show total price and visually indicate if it is possible to buy
      val totalPrice = (bookPrice * bookAmount) + (plantPrice * plantAmount) + (decorationPrice * decotrationAmount)
      val color = if (totalPrice > userPoints) PriorityRed else MaterialTheme.colorScheme.surfaceVariant
      Text(
        text = "Gesamt - $totalPrice Punkte",
        style = MaterialTheme.typography.bodySmall,
        color = color,
      )

      // Buy button
      ActionButton(
        onClick = {
          if (totalPrice > 0 && totalPrice <= userPoints) {
            authViewModel.buyShopItems(
              totalPrice = totalPrice,
              bookAmount = bookAmount,
              plantAmount = plantAmount,
              decorationAmount = decotrationAmount,
              onSuccess = {
                onOpenBookshelf()

                // Clear basket after successful purchase
                bookAmount = 0
                plantAmount = 0
                decotrationAmount = 0
              }
            )
          }
        },
        modifier = Modifier.width(170.dp),
        leadingIcon = painterResource(R.drawable.shopping_cart),
        text = "Kaufen",
        isPrimary = true,
        enabled = totalPrice <= userPoints
      )
    }
  }
}