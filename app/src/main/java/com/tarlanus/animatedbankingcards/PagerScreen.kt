package com.tarlanus.animatedbankingcards

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.tarlanus.animatedbankingcards.ui.theme.BackG
import com.tarlanus.animatedbankingcards.ui.theme.Color1Left
import com.tarlanus.animatedbankingcards.ui.theme.Color1Right
import com.tarlanus.animatedbankingcards.ui.theme.Color2Left
import com.tarlanus.animatedbankingcards.ui.theme.Color2Right
import com.tarlanus.animatedbankingcards.ui.theme.Color3Left
import com.tarlanus.animatedbankingcards.ui.theme.Color3Right


import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
@Preview(showBackground = true)
fun PagerScreen() {


    val listColors = remember {
        mutableStateListOf(
            Pair(Color1Left, Color1Right), Pair(
                Color2Left,
                Color2Right
            ), Pair(Color3Left, Color3Right)
        )
    }

    val currentCard = remember { mutableStateOf<Int?>(null) }
    val currentFlip = remember { mutableStateOf<Int?>(null) }

    val scope = rememberCoroutineScope()
    val isAnimating = remember { mutableStateOf<Int?>(null) }
    val dragProgress = remember { mutableStateOf(0f) }
    val draggingOffset = remember { mutableStateOf(0f) }


    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(BackG)
        .safeContentPadding()) {


        val (box, current) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(box) {
                    bottom.linkTo(parent.bottom, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                }
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            listColors.forEachIndexed { index, item ->
                var setOfset = ((index + 1) * (25))
                val setPadding = ((listColors.size - index) * 11)

                val translationY = animateFloatAsState(
                    targetValue = if (currentCard.value == index) -600f else 0f,
                    animationSpec = tween(delayMillis = 200, durationMillis = 2000),
                    label = "translationY"
                )

                val scaleAnimY = animateFloatAsState(
                    targetValue = if (isAnimating.value == index) 0.7f else 1f,
                    tween(delayMillis = 100, durationMillis = 1000), label = "scaleY"
                )
                val scaleAnimX = animateFloatAsState(
                    targetValue = if (isAnimating.value == index) 0.7f else 1f,
                    tween(delayMillis = 100, durationMillis = 1000), label = "scaleX"
                )

                val rotationYAnimClosed = animateFloatAsState(
                    targetValue = if (currentCard.value == index) 1440f else 0f,
                    animationSpec = tween(delayMillis = 100, durationMillis = 2000),
                    label = "rotationY"
                )
                val rotationYAnimOpened = animateFloatAsState(
                    targetValue = if (currentFlip.value == index) 180f else 0f,
                    animationSpec = tween(delayMillis = 100, durationMillis = 900),
                    label = "rotationY"
                )
                val setRotationY = if (currentFlip.value != index) rotationYAnimClosed else rotationYAnimOpened
                val rotationZAnim = animateFloatAsState(
                    targetValue = if (currentCard.value == index) 360f else 0f,
                    animationSpec = tween(delayMillis = 100, durationMillis = 2000),
                    label = "rotationZ",
                    finishedListener = {
                        isAnimating.value = null
                    })
                var translationX by remember { mutableStateOf(0f) }
                var setAlpha by remember { mutableStateOf(1f) }

                var setScale = 1f
                if (currentCard.value != index) {
                    setScale = setScale + (0.12f * dragProgress.value)
                }

                val setSide = remember { mutableStateOf("front") }






                CardItem(
                    modifier = Modifier
                        .graphicsLayer(
                            translationY = translationY.value,
                            scaleX = scaleAnimX.value * setScale,
                            scaleY = scaleAnimY.value * setScale,
                            rotationY = setRotationY.value,
                            rotationZ = rotationZAnim.value,
                            translationX = translationX,
                            cameraDistance = 12f * LocalDensity.current.density,
                        )
                        .padding(setPadding.dp)
                        .offset(y = setOfset.dp)
                        .offset(y = draggingOffset.value.dp)

                        .fillMaxWidth()
                        .height(180.dp)
                        .alpha(setAlpha)

                        .clickable {
                            if (currentCard.value == index) {
                                currentCard.value = null
                            } else {
                                currentCard.value = index
                            }
                            scope.launch {
                                if (isAnimating.value == index) {
                                    isAnimating.value = null

                                } else {
                                    isAnimating.value = index
                                    delay(750)
                                    isAnimating.value = null

                                }
                            }

                        }
                        .pointerInput(Unit) {


                            detectDragGestures(onDrag = { change, dragAmount ->
                                Log.e("getindexes", "current ${currentCard.value}")
                                Log.e("getindexes", "index ${index}")

                                if (currentCard.value != index) {
                                    change.consume()
                                    translationX = translationX + dragAmount.x

                                    val progress = (abs(translationX) / 1000f).coerceIn(0f, 1f)
                                    setAlpha = 1f - progress
                                    dragProgress.value = progress
                                    draggingOffset.value = abs(progress * 100)


                                }


                            }, onDragEnd = {
                                if (currentCard.value != index) {
                                    val getOffset = abs(translationX.toInt())
                                    Log.e("getOffsetS", "getOffset ${setOfset}")

                                    if (getOffset > 250) {
                                        val getThis = listColors.get(index)
                                        listColors.remove(listColors.get(index))

                                        listColors.add(0, getThis)


                                    }
                                    dragProgress.value = 0f
                                    draggingOffset.value = 0f
                                    setAlpha = 1f
                                    translationX = 0f

                                    Log.e("getDragging", "offsetX $translationX")

                                    Log.e("getDragging", "onDragEnd}")

                                } else {
                                    Log.e("getDraggingFlip", "onDragEnd}")
                                    scope.launch {
                                        delay(500)
                                        if (setSide.value == "front") {
                                            setSide.value = "back"
                                        } else if (setSide.value == "back") {
                                            setSide.value = "front"
                                        }
                                    }

                                    if (currentFlip.value == index) {
                                        currentFlip.value = null

                                    } else {
                                        currentFlip.value = index
                                        scope.launch {
                                            delay(500)
                                            currentFlip.value = null

                                        }
                                    }
                                }

                            })


                        },
                    setColor = listColors.get(index),
                    getSide = setSide.value
                )

            }

        }
    }


}
