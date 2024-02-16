package pt.ipp.estg.peddypaper.ui.Navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.ui.Contacto.ContactarScreen
import pt.ipp.estg.peddypaper.ui.LeaderBoard.LeaderBoardScreen1
import pt.ipp.estg.peddypaper.ui.Login.LoginPage
import pt.ipp.estg.peddypaper.ui.Login.LoginViewModel
import pt.ipp.estg.peddypaper.ui.Login.SignUp
import pt.ipp.estg.peddypaper.ui.MapScreen.MapScreen1
import pt.ipp.estg.peddypaper.ui.MenuPrincipal.MenuPrincipal
import pt.ipp.estg.peddypaper.ui.Profile.ProfileScreen
import pt.ipp.estg.peddypaper.ui.QuestionScreen.QuestionScreenByQuestionNumber
import pt.ipp.estg.peddypaper.ui.QuestionScreen.QuestionScreenByQuestionNumberPaisagem
import pt.ipp.estg.peddypaper.ui.Regras.Regras

@Composable
@Preview
fun MyNavigatonDrawerPreview() {
    MyNavigatonDrawer()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNavigatonDrawer() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val drawerItemList = prepareNavigationDrawerItems()
    var selectedItem by remember { mutableStateOf(drawerItemList[0]) }

    ModalNavigationDrawer(
        gesturesEnabled = false,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                drawerItemList.forEach { item ->
                    MyDrawerItem(
                        item,
                        selectedItem,
                        { selectedItem = it },
                        navController,
                        drawerState
                    )
                }
            }
        },
        content = { MyScaffold(drawerState = drawerState, navController = navController) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawerItem(
    item: NavigationDrawerData,
    selectedItem: NavigationDrawerData,
    updateSelected: (i: NavigationDrawerData) -> Unit,
    navController: NavHostController,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()
    NavigationDrawerItem(
        icon = { Icon(imageVector = item.icon, contentDescription = null) },
        label = { Text(text = item.label) },
        selected = (item == selectedItem),
        onClick = {
            coroutineScope.launch {
                navController.navigate(item.label)
                drawerState.close()
            }
            updateSelected(item)
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(drawerState: DrawerState, navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var showtopBar by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            if (showtopBar) {
                MyTopAppBar(navController) {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                MyScaffoldContent(navController, { showtopBar = it })
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(navController: NavHostController, onNavIconClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Peddy Paper: Felgueiras",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
                color = Color.White // Cor do texto
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onNavIconClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Items",
                    tint = Color.White // Cor do ícone
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("profileScreen")
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White // Cor do ícone
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(219, 143, 20, 255)
        ),
        modifier = Modifier.shadow(4.dp)
    )
}

@Composable
fun MyScaffoldContent(navController: NavHostController, setTopBarVisible: (x: Boolean) -> Unit) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val viewModel: LoginViewModel = viewModel()
    val authStatus = viewModel.authState.observeAsState()

    NavHost(
        navController = navController,
        startDestination = if (viewModel.fAuth.currentUser == null) "loginPage" else "Menu Principal"
    ) {
        composable("loginPage") {
            LoginPage(navController)
            setTopBarVisible(false)
        }
        composable("registo") {
            SignUp(navController)
            setTopBarVisible(false)
        }
        composable("Menu Principal") {
            MenuPrincipal(navController)
            setTopBarVisible(true)
        }
        composable("profileScreen") {
            ProfileScreen(navController)
            setTopBarVisible(true)
        }
        composable("Regras") {
            Regras()
            setTopBarVisible(true)
        }
        composable("Jogar") {
            MapScreen1(navController)
            setTopBarVisible(true)
        }
        composable("Tabela de Classificação") {
            LeaderBoardScreen1()
            setTopBarVisible(true)
        }
        composable("Contactar Administração") {
            ContactarScreen()
            setTopBarVisible(true)
        }
        composable(
            "question/{numberQuestion}",
            arguments = listOf(navArgument("numberQuestion") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            if (isPortrait) {
                QuestionScreenByQuestionNumber(
                    navController,
                    backStackEntry.arguments!!.getInt("numberQuestion")
                )
            } else {
                QuestionScreenByQuestionNumberPaisagem(
                    navController,
                    backStackEntry.arguments!!.getInt("numberQuestion")
                )
            }
            setTopBarVisible(false)
        }
        composable("Logout") {
            viewModel.logout()
            LoginPage(navController)
            setTopBarVisible(false)
        }
    }
}

private fun prepareNavigationDrawerItems(): List<NavigationDrawerData> {
    val drawerItemsList = arrayListOf<NavigationDrawerData>()
    drawerItemsList.add(NavigationDrawerData(label = "Menu Principal", icon = Icons.Filled.Home))
    drawerItemsList.add(NavigationDrawerData(label = "Regras", icon = Icons.Filled.Info))
    drawerItemsList.add(NavigationDrawerData(label = "Jogar", icon = Icons.Filled.SportsEsports))
    drawerItemsList.add(
        NavigationDrawerData(
            label = "Tabela de Classificação",
            icon = Icons.Filled.EmojiEvents
        )
    )
    drawerItemsList.add(
        NavigationDrawerData(
            label = "Contactar Administração",
            icon = Icons.Filled.Chat
        )
    )
    drawerItemsList.add(
        NavigationDrawerData(
            label = "Logout",
            icon = Icons.Filled.Logout
        )
    )
    return drawerItemsList
}

data class NavigationDrawerData(val label: String, val icon: ImageVector)
