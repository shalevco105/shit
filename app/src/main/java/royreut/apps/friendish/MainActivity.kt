package royreut.apps.friendish

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import royreut.apps.friendish.base.MyApplication
import royreut.apps.friendish.models.FirebaseModel
import royreut.apps.friendish.models.Model
import royreut.apps.friendish.models.User
import royreut.apps.friendish.modules.auth.LoginFragmentDirections
import royreut.apps.friendish.modules.userDishes.UserDishesFragmentDirections

class MainActivity : AppCompatActivity() {
    private var navController:NavController? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContentView(R.layout.activity_main)

        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment
        navController = navHostFragment?.navController

        navController?.let {
            NavigationUI.setupActionBarWithNavController(this, it)
        }


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }

        navController?.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.loginFragment || nd.id == R.id.signUpFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }


        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.email?.let { Model.instance.getUserByEmail(it) }
            val navOptions:NavOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
            val action = LoginFragmentDirections.actionLoginFragmentToDishesFragment3()
            navController?.navigate(action, navOptions)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentUser = auth.currentUser

        return when (item.itemId) {
            android.R.id.home -> {
                navController?.navigateUp()
                true
            }
            R.id.logoutBtn -> {
                auth.signOut()
                MyApplication.Globals.user = null
                navController?.navigate(R.id.loginFragment)
                true
            }

            else -> {
                if (currentUser != null) {
                    navController?.let { NavigationUI.onNavDestinationSelected(item, it) } ?: super.onOptionsItemSelected(item)
                }
                return true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}