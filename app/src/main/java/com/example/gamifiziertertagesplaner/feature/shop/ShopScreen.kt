package com.example.gamifiziertertagesplaner.feature.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

@Composable
fun ShopScreen(
  authViewModel: AuthViewModel,
  onOpenHome: () -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenAchievements: () -> Unit,
) {
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
      var bookAmount by remember { mutableIntStateOf(0) }
      var plantAmount by remember { mutableIntStateOf(0) }
      var decotrationAmount by remember { mutableIntStateOf(0) }

      val bookPrice = 200
      val plantPrice = 500
      val decorationPrice = 750

      val profilePoints = authViewModel.userProfile?.userPoints ?: 0

      TopScreenTitle(title = "Buchladen")

      // Punktestand
      Text(
        text = "Punktestand",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Text(
        text = profilePoints.toString(),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(12.dp))

      ItemShopSection(
        item = "Buch",
        price = bookPrice,
        itemAmount = bookAmount,
        onIncreaseAmount = { bookAmount += 1 },
        onReduceAmount = { bookAmount -= 1 }
      )
      ItemShopSection(
        item = "Pflanze",
        price = plantPrice,
        itemAmount = plantAmount,
        onIncreaseAmount = { plantAmount += 1 },
        onReduceAmount = { plantAmount -= 1 }
      )
      ItemShopSection(
        item = "Dekoration",
        price = decorationPrice,
        itemAmount = decotrationAmount,
        onIncreaseAmount = { decotrationAmount += 1 },
        onReduceAmount = { decotrationAmount -= 1 }
      )

      Spacer(modifier = Modifier.height(12.dp))

      val totalPrice = (bookPrice * bookAmount) + (plantPrice * plantAmount) + (decorationPrice * decotrationAmount)
      val color = if (totalPrice > profilePoints) PriorityRed else MaterialTheme.colorScheme.surfaceVariant

      Text(
        text = "Gesamt - $totalPrice Punkte",
        style = MaterialTheme.typography.bodySmall,
        color = color,
      )

      ActionButton(
        onClick = {
          if (totalPrice > 0 && totalPrice <= profilePoints) {
            authViewModel.buyShopItems(
              totalPrice = totalPrice,
              bookAmount = bookAmount,
              plantAmount = plantAmount,
              decorationAmount = decotrationAmount,
              onSuccess = {
                // Clear amounts
                bookAmount = 0
                plantAmount = 0
                decotrationAmount = 0

                onOpenBookshelf()
              }
            )
          }
        },
        modifier = Modifier.width(170.dp),
        leadingIcon = painterResource(R.drawable.shopping_cart),
        text = "Kaufen",
        isPrimary = true,
        enabled = totalPrice <= profilePoints
      )
    }
  }
}

@Composable
private fun ItemShopSection(
  item: String,
  price: Int,
  itemAmount: Int,
  onIncreaseAmount: () -> Unit,
  onReduceAmount: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp, horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    ItemShopButton(
      modifier = Modifier.weight(1f),
      item = item,
      price = price,
      onClick = onIncreaseAmount
    )

    if (itemAmount > 0) {
      ItemAmount(
        itemAmount = itemAmount,
        onIncreaseAmount = onIncreaseAmount,
        onReduceAmount = onReduceAmount
      )
    }
  }
}

@Composable
private fun ItemShopButton(
  modifier: Modifier,
  item: String,
  price: Int,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Item icon
      Icon(
        modifier = Modifier.size(60.dp),
        imageVector = Icons.Default.Circle,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSecondary
      )

      // Title and time information
      Column(
        modifier = Modifier
          .padding(horizontal = 5.dp)
          .weight(1f)
      ) {
        // Item title
        Text(
          text = item,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSecondary
        )

        // Item price
        Text(
          text = "$price Punkte",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.surfaceVariant
        )
      }
    }
  }
}

@Composable
private fun ItemAmount(
  itemAmount: Int,
  onIncreaseAmount: () -> Unit,
  onReduceAmount: () -> Unit
) {
  Row(
    modifier = Modifier.padding(start = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = onReduceAmount,
      modifier = Modifier.size(24.dp),
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(R.drawable.minus),
        tint = MaterialTheme.colorScheme.surfaceVariant,
        contentDescription = "subtract"
      )
    }

    Text(
      modifier = Modifier.padding(horizontal = 12.dp),
      text = itemAmount.toString(),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.surfaceVariant
    )

    IconButton(
      onClick = onIncreaseAmount,
      modifier = Modifier.size(24.dp),
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(R.drawable.plus),
        tint = MaterialTheme.colorScheme.surfaceVariant,
        contentDescription = "add"
      )
    }
  }
}