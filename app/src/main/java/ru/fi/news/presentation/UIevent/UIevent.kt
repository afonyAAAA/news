package ru.fi.news.presentation.UIevent

sealed class UIevent(){
    data class ShowWebView(val url : String) : UIevent()
    object HideWebView : UIevent()

}
