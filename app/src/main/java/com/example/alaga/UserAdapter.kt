import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alaga.R
import com.yourpackage.models.User


class UserAdapter(private var userList: List<User>, private val listener: UserClickListener) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface UserClickListener {
        fun onEdit(user: User)
        fun onDelete(userId: Int)
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvUserName)
        val role: TextView = view.findViewById(R.id.tvUserRole)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.name.text = user.name
        holder.role.text = user.role
        holder.btnEdit.setOnClickListener { listener.onEdit(user) }
        holder.btnDelete.setOnClickListener { listener.onDelete(user.id) }
    }

    override fun getItemCount(): Int = userList.size
}
