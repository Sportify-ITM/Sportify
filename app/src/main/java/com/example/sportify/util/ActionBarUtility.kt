import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView
import com.example.sportify.R

object ActionBarUtility {

    fun setLogo(activity: AppCompatActivity, logoResId: Int) {
        // Retrieve the Toolbar and ImageView from the layout
        val toolbar = activity.findViewById<Toolbar>(R.id.my_toolbar)
        val toolbarImg = activity.findViewById<ImageView>(R.id.toolbar_title_image)
        toolbarImg.setImageResource(logoResId)
        toolbar.title = ""
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}
