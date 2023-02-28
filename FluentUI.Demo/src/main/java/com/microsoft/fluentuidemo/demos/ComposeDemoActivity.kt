package com.microsoft.fluentuidemo.demos

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.token.controlTokens.AvatarCarouselSize
import com.microsoft.fluentui.theme.token.controlTokens.ButtonStyle
import com.microsoft.fluentui.tokenized.controls.Button
import com.microsoft.fluentui.tokenized.persona.AvatarCarousel
import com.microsoft.fluentui.tokenized.persona.AvatarCarouselItem
import com.microsoft.fluentui.tokenized.persona.Person
import com.microsoft.fluentui.tokenized.progress.CircularProgressIndicator
import com.microsoft.fluentuidemo.DemoActivity
import com.microsoft.fluentuidemo.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class ComposeDemoActivity : DemoActivity() {
    override val contentLayoutId: Int
        get() = R.layout.v2_activity_compose
    override val contentNeedsScrollableContainer: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val composeHere = findViewById<ComposeView>(R.id.compose_here)
        var userViewModel = UserViewModel(FakeUserRepository())
        composeHere.setContent {
            FluentTheme {
                //UI State at UI level
                var show by rememberSaveable { mutableStateOf(false) }
                var buttonText by rememberSaveable { mutableStateOf("Show User") }

                //UI Element components
                Column(Modifier.padding(16.dp)) {
                    Button(text = buttonText,
                        style = ButtonStyle.OutlinedButton,
                        onClick = {
                            userViewModel.fetchUser()
                            show = !show
                            buttonText = if (show) "Hide User" else "Show User"

                        }
                    )
                    AnimatedVisibility(visible = show) {
                        Column {
                            Text("All Users", fontWeight = FontWeight.Bold)
                            if (userViewModel.uiState.loading) {
                                CircularProgressIndicator()
                            } else {
                                AvatarCarousel(
                                    avatarList = userViewModel.uiState.users.map { person ->
                                        AvatarCarouselItem(
                                            person = person
                                        )
                                    },
                                    AvatarCarouselSize.Medium
                                )
                            }
                            Text("Online Users", fontWeight = FontWeight.Bold)
                            AvatarCarousel(
                                avatarList = userViewModel.onlineUser.collectAsState().value.map { person ->
                                    AvatarCarouselItem(person = person)
                                },
                                AvatarCarouselSize.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun showUsers(text: String, show: Boolean, onClick: () -> Unit, userViewModel: UserViewModel) {
        //UI Element components
        Column(Modifier.padding(16.dp)) {
            Button(
                text = text,
                style = ButtonStyle.OutlinedButton,
                onClick = onClick
            )

            AnimatedVisibility(visible = show) {
                Column {
                    Text("All Users", fontWeight = FontWeight.Bold)
                    if (userViewModel.uiState.loading) {
                        CircularProgressIndicator()
                    } else {
                        AvatarCarousel(
                            avatarList = userViewModel.uiState.users.map { person ->
                                AvatarCarouselItem(
                                    person = person
                                )
                            },
                            AvatarCarouselSize.Medium
                        )
                    }
                    Text("Online Users", fontWeight = FontWeight.Bold)
                    AvatarCarousel(
                        avatarList = userViewModel.onlineUser.collectAsState().value.map { person ->
                            AvatarCarouselItem(person = person)
                        },
                        AvatarCarouselSize.Medium
                    )
                }
            }
        }
    }

    interface UserRepository {
        fun observeOnlineUser(): Flow<Set<Person>>
        fun users(): Set<Person>
    }

    class FakeUserRepository : UserRepository {
        private val person = setOf(
            Person(
                "Allan", "Munger",
                image = R.drawable.avatar_allan_munger
            ),
            Person(
                "Wanda", "Howard",
                image = R.drawable.avatar_wanda_howard
            ),
            Person(
                "Kat", "Larson"
            ),
            Person(
                "Daisy", "Phillips",
                image = R.drawable.avatar_daisy_phillips
            ),
            Person(
                "Mona", "Kane",
                image = R.drawable.avatar_mona_kane
            )
        )
        private val onlinePerson = MutableStateFlow(setOf<Person>())

        init {
            GlobalScope.launch {
                while (true) {
                    delay(3000)
                    onlinePerson.update {
                        person.filter { (Math.random() * 10).toInt() % 2 != 0 }.toSet()
                    }
                }
            }
        }

        override fun observeOnlineUser(): Flow<Set<Person>> {
            return onlinePerson
        }

        override fun users() = person
    }

    data class UserUiState(
        val users: List<Person> = listOf(),
        val loading: Boolean = true
    )

    class UserViewModel(
        private val repository: UserRepository
    ) : ViewModel() {
        var uiState by mutableStateOf(UserUiState())
            private set

        private var fetchJob: Job? = null

        fun fetchUser() {
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                try {
                    uiState.copy(loading = true)
                    delay(4000)
                    val users = repository.users().toList()
                    uiState = uiState.copy(users = users, false)
                } catch (ioe: IOException) {
                    uiState = uiState.copy(users = listOf(), false)
                }
            }
        }

        val onlineUser = repository.observeOnlineUser().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }
}