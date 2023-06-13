package com.project.cafehub.view.cafe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cafehub.R
import com.project.cafehub.adapter.RatingAdapter
import com.project.cafehub.databinding.FragmentCafeCommentsBinding
import com.project.cafehub.model.Rating
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.Comparator

class CafeCommentsFragment : Fragment(R.layout.fragment_cafe_comments) {

    private lateinit var binding : FragmentCafeCommentsBinding
    private lateinit var db: FirebaseFirestore
    private var currentCafeId : String? = null
    private lateinit var ratingList: ArrayList<Rating>
    private lateinit var ratingAdapter: RatingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCafeCommentsBinding.bind(view)
        db = Firebase.firestore
        val bundle = arguments
        currentCafeId = bundle?.getString("currentCafeId")
        ratingList = ArrayList()

        binding.rvComments.setHasFixedSize(true)
        val commentLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvComments.layoutManager = commentLayoutManager
        ratingAdapter = RatingAdapter(ratingList)
        binding.rvComments.adapter = ratingAdapter
        setRatingList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRatingList() {
        db.collection("Rating").whereEqualTo("cafeId", currentCafeId)
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.id
                    val comment = document.get("comment") as String
                    val score = (document.get("score") as Long).toInt()
                    val userId = document.get("userId") as String
                    val cafeId = document.get("cafeId") as String
                    val ratingDate = (document.get("ratingDate") as Timestamp).toDate()
                    val passingTime = calculatePassingTime(ratingDate)
                    val rating = Rating(id, comment, score, userId, cafeId, ratingDate, passingTime)
                    ratingList.add(rating)
                }
                editCafeRating()
                sortRatingList()
                ratingAdapter.notifyDataSetChanged()
            }.addOnFailureListener{
                Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun editCafeRating() {
        var scoreSum: Double = 0.0
        for (rating in ratingList) {
            scoreSum += rating.score!!
        }


        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val roundoff = df.format(scoreSum / ratingList.size.toDouble())

        binding.tvCafeRating.text = (roundoff).toString()
    }

    fun calculatePassingTime(ratingDate: Date) : Long?{
        val diff: Long = Date().time - ratingDate.time
        return diff
    }

    fun sortRatingList() {
        ratingList.sortWith { o1, o2 ->
            o1.passingTime!!.compareTo(o2.passingTime!!)
        }
    }
}