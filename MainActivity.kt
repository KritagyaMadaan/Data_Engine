class MainActivity : AppCompatActivity() {
    private lateinit var meetingInput: EditText
    private lateinit var extractButton: Button
    private lateinit var actionList: RecyclerView
    
    private val actions = mutableListOf<ActionItem>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        meetingInput = findViewById(R.id.meetingInput)
        extractButton = findViewById(R.id.extractButton)
        actionList = findViewById(R.id.actionList)
        
        actionList.layoutManager = LinearLayoutManager(this)
        actionList.adapter = ActionAdapter(actions)
        
        extractButton.setOnClickListener { extractActionItems() }
    }
    
    private fun extractActionItems() {
        val input = meetingInput.text.toString().trim()
        if (input.isEmpty()) return
        
        extractButton.isEnabled = false
        extractButton.text = "Extracting with Local AI..."
        
        CoroutineScope(Dispatchers.Main).launch {
            val result = parseMeetingNotes(input)
            if (result != null) {
                actions.clear()
                actions.addAll(result)
                actionList.adapter?.notifyDataSetChanged()
                meetingInput.text.clear()
                Toast.makeText(this@MainActivity, "✅ ${result.size} action items extracted!", Toast.LENGTH_LONG).show()
            }
            extractButton.isEnabled = true
            extractButton.text = "Extract Action Items → JSON"
        }
    }
    
    private suspend fun parseMeetingNotes(input: String): List<ActionItem>? = withContext(Dispatchers.IO) {
        val prompt = """
            Extract action items from meeting notes. Return ONLY valid JSON array.
            
            Notes: $input
            
            Format - each item must have owner:
            [
              {
                "owner": "person name", 
                "task": "what to do",
                "due": "today|tomorrow|Friday|next week|ASAP"
              }
            ]
            
            Examples:
            - "Sarah do slides" → {"owner": "Sarah", "task": "do slides", "due": "ASAP"}
            - "I will call client tomorrow" → {"owner": "You", "task": "call client", "due": "tomorrow"}
        """.trimIndent()
        
        val jsonResponse = callRunAnywhereLLM(prompt)
        
        return@withContext try {
            val jsonArray = JSONArray(jsonResponse)
            (0 until jsonArray.length()).mapNotNull {
                val obj = jsonArray.getJSONObject(it)
                ActionItem(
                    owner = obj.getString("owner"),
                    task = obj.getString("task"),
                    due = obj.getString("due")
                )
            }
        } catch (e: Exception) { null }
    }
    
    private suspend fun callRunAnywhereLLM(prompt: String): String {
        // 🔥 REAL RunAnywhere SDK CALL (check starter repo docs)
        // return RunAnywhere.generateText(model = "llama3", prompt = prompt)
        
        // 🧪 MVP Testing - Replace with above when SDK ready
        return """[
            {"owner": "Sarah", "task": "client call", "due": "tomorrow"},
            {"owner": "You", "task": "finish slides", "due": "Friday"},
            {"owner": "John", "task": "check budget", "due": "next week"}
        ]"""
    }
}
