package knu.dong.capstone2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import knu.dong.capstone2.adapter.RolesAdapter
import knu.dong.capstone2.common.HttpRequestHelper
import knu.dong.capstone2.databinding.ActivitySelectChatbotBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class SelectChatbotActivity: AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var binding: ActivitySelectChatbotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.titleBar.btnBack.visibility = View.INVISIBLE

        job = Job()

        getChatbotRoles()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun getChatbotRoles() {
        launch(Dispatchers.Main) {
            val roles: List<String> =
                HttpRequestHelper()
                    .get<List<String>>("api/chatbots/roles")
                    ?: emptyList()

            val adapter = RolesAdapter(this@SelectChatbotActivity, roles)
            binding.listView.adapter = adapter

            binding.listView.onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this@SelectChatbotActivity, ChatbotActivity::class.java)
                    intent.putExtra("role", adapter.getItem(position))

                    startActivity(intent)
                }
        }
    }
}