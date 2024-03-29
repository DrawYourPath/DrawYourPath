package com.epfl.drawyourpath.mainpage.fragments.helperClasses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.mainpage.fragments.FriendsFragment

/**
 * A custom FragmentFactory class responsible for creating instances of FriendsFragment and other classes that require database dependency injection.
 *
 * @param database The shared Database object to be injected into the fragments.
 */
class CustomFragmentFactory(private val database: Database) : FragmentFactory() {

    /**
     * This method is responsible for instantiating the appropriate Fragment based on the provided
     * class name. It handles the creation of FriendsFragment, injecting the
     * required Database object into their constructors.
     *
     * @param classLoader The ClassLoader used to load the Fragment class.
     * @param className The class name of the Fragment to be instantiated.
     * @return A new Fragment instance of the specified class name with the required dependencies,
     *         or calls the default FragmentFactory implementation for other class names.
     */
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            // Add other fragments that require a database argument here
            FriendsFragment::class.java.name -> FriendsFragment(database)
            else -> super.instantiate(classLoader, className)
        }
    }
}
