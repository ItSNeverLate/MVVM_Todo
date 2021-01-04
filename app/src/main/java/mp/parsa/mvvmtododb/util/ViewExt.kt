package mp.parsa.mvvmtododb.util

import androidx.appcompat.widget.SearchView

// inline to more efficiently not functionality (inline the function body wherever used)
// crossinline: none-local return
inline fun SearchView.setOnQueryTextChanged(crossinline listener: (String) -> Unit) {
    // listener("") this is a local return
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}