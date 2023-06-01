package com.project.cafehub.view.homePage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.cafehub.databinding.FragmentSettingsBinding
import com.project.cafehub.model.CurrentUser
import com.project.cafehub.model.User
import com.project.cafehub.view.authentication.SigninActivity
import com.project.cafehub.view.friendship.FriendshipActivity
import com.project.cafehub.view.settings.ProfileSettingsActivity
import com.squareup.picasso.Picasso
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth= Firebase.auth
        displayUserInfo()
        setOnClickListener()
    }
    override fun onResume() {
        super.onResume()
        displayUserInfo()
    }

    private fun displayUserInfo(){
        Picasso.get().load(CurrentUser.user.photoUrl).into(binding.userPhoto)

        binding.userName.text= (CurrentUser.user.name?.capitalize(Locale.ROOT)) +
                " " + (CurrentUser.user.surname?.capitalize(Locale.ROOT))
    }
    fun redirectToProfileSettings(){
        val intent = Intent(requireContext(), ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
    fun redirectToHistory(){
        val intent = Intent(requireContext(), ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
    fun redirectToFriends(){
        val intent = Intent(requireContext(), FriendshipActivity::class.java)
        startActivity(intent)
    }
    fun redirectToAppSettings(){
        val intent = Intent(requireContext(), ProfileSettingsActivity::class.java)
        startActivity(intent)
    }

    fun logout(){
        auth.signOut()
        CurrentUser.user = User()
        val intent = Intent(requireContext(), SigninActivity::class.java);
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setOnClickListener() {

        binding.profileCardView.setOnClickListener {

            redirectToProfileSettings()
        }

        binding.historyCardView.setOnClickListener {
            redirectToHistory()
        }

        binding.friendsCardView.setOnClickListener{
            redirectToFriends()
        }
        binding.settingsCardView.setOnClickListener {
            redirectToAppSettings()
        }

        binding.logoutCardView.setOnClickListener{
            logout()
        }
    }

}