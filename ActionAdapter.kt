class ActionAdapter(private val actions: List<ActionItem>) : 
    RecyclerView.Adapter<ActionAdapter.ViewHolder>() {
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = actions[position]
        holder.itemView.findViewById<TextView>(android.R.id.text1).text = 
            "👤 ${action.owner}: ${action.task} (${action.due})"
    }
    
    override fun getItemCount() = actions.size
}
