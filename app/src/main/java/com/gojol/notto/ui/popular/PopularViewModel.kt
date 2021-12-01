package com.gojol.notto.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nottokeyword.Keyword
import com.example.nottokeyword.KeywordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(private val keywordRepository: KeywordRepository) :
    ViewModel() {

    private val _items = MutableLiveData<List<Keyword>?>()
    val items: LiveData<List<Keyword>?> = _items

    fun fetchKeywords() {
        viewModelScope.launch {
            keywordRepository.getKeywords(::onGetKeywords)
        }
    }

    private fun onGetKeywords(keywords: List<Keyword>) {
        _items.value = keywords
    }
}
