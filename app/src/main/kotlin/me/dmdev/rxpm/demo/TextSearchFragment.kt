package me.dmdev.rxpm.demo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author Dmitriy Gorbunov
 */
class TextSearchFragment : Fragment() {

    private val pm = TextSearchPresentationModel()
    private var composite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        pm.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindPresentationModel()
    }

    fun onBindPresentationModel() {

        // --- States ---
        pm.foundWordState
                .subscribe {
                    if (it.isNotEmpty()) {
                        resultText.text = it.joinToString(separator = "\n")
                    } else {
                        resultText.text = "Nothing found"
                    }
                }
                .addTo(composite)

        pm.searchButtonEnabledState
                .subscribe(searchButton.enabled())
                .addTo(composite)

        pm.loadingState
                .subscribe(progressBar.visibility())
                .addTo(composite)
        // ---------------


        // --- Ui-events ---
        queryEditText
                .textChanges()
                .map { it.toString() }
                .subscribe(pm.searchQueryConsumer)
                .addTo(composite)

        inputText
                .textChanges()
                .map { it.toString() }
                .subscribe(pm.inputTextChangesConsumer)
                .addTo(composite)

        searchButton.clicks()
                .subscribe(pm.searchButtonClicksConsumer)
                .addTo(composite)
        //------------------
    }

    fun onUnbindPresentationModel() {
        composite.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onUnbindPresentationModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        pm.onDestroy()
    }
}
