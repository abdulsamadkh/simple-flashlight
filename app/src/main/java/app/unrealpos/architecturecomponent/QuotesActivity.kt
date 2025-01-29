package app.unrealpos.architecturecomponent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class QuotesActivity : AppCompatActivity() {

    private val quoteText: TextView
        get()=findViewById(R.id.quoteText)

    private val quoteAuthor: TextView
        get()=findViewById(R.id.quoteAuthor)

    private lateinit  var myViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        myViewModel= ViewModelProvider(this,MainViewModelFactory(application))[MyViewModel::class.java]
       setQuote(myViewModel.getQuote())

    }

    private fun setQuote(quote: Quote) {
        quoteText.text = quote.text
        quoteAuthor.text = quote.author
    }

    fun onNext(view: View) {
        setQuote(myViewModel.nextQuote())
    }
    fun onShare(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, myViewModel.getQuote().toString())
        startActivity(intent)
    }
    fun onPrevious(view: View) {
        setQuote(myViewModel.previousQuote())
        println("on previous tapp")
    }

}