package com.tarlanus.animatedbankingcards

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlin.random.Random

@Composable
fun CardItem(
    modifier: Modifier,
    cardNUM: String = "1111 1111 1111 1111",
    expDATE: String = "09/29",
    setColor: Pair<Color, Color> = Pair(Color(Random.nextInt(256)), Color(Random.nextInt(256))),
    getSide : String = "front"
) {


    val isFront = remember { mutableStateOf(true) }
    LaunchedEffect(getSide) {
        Log.e("onLaunchSide", "getSide $getSide")
        if (getSide == "front") {
            isFront.value = true
        } else {
            isFront.value = false
        }
    }

    Card(modifier = modifier
        ,
        shape = RoundedCornerShape(15.dp),


        ) {

        if (isFront.value) {
            ConstraintLayout(modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(colors = listOf(setColor.first, setColor.second)))) {

                val (holder, num, date) = createRefs()

                Text(text = "TARLAN ABASZADA", color = Color.White, textAlign = TextAlign.Center,
                    fontSize = 20.sp, modifier = Modifier.constrainAs(holder) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Text(text = cardNUM, color = Color.White, textAlign = TextAlign.Center,
                    fontSize = 22.sp, modifier = Modifier.constrainAs(num) {
                        top.linkTo(holder.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(date.top)
                    }
                )

                Text(text = expDATE, color = Color.White, textAlign = TextAlign.Center,
                    fontSize = 20.sp, modifier = Modifier.constrainAs(date) {

                        end.linkTo(parent.end, margin = 20.dp)
                        bottom.linkTo(parent.bottom, margin = 15.dp)
                    }
                )





            }

        } else {
            ConstraintLayout(modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(colors = listOf(setColor.second, setColor.first)))) {

                val (holder, barcode) = createRefs()

                Text(text = "TARLAN ABASZADA", color = Color.White, textAlign = TextAlign.Center,
                    fontSize = 24.sp, modifier = Modifier.constrainAs(holder) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )


                Card (modifier = Modifier.constrainAs(barcode){
                    top.linkTo(holder.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                }.padding(50.dp), shape = RoundedCornerShape(15.dp)) {

                    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                        Text(textAlign = TextAlign.Center, fontSize = 24.sp, color = Color.Black, text = "XXXXXXXXXXXXXXXXX")
                    }

                }





            }

        }



    }


}


@Composable
@Preview(showBackground = true)
fun PreviewCardItem() {
    CardItem(Modifier)
}