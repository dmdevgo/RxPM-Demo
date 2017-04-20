package me.dmdev.rxpm.demo

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * @author Dmitriy Gorbunov
 */
class TextSearchPresentationModel {

    private val interactor: TextSearchInteractor = TextSearchInteractorImpl()

    // --- States ---
    private val foundWords = BehaviorRelay.create<List<String>>()
    val foundWordState: Observable<List<String>> = foundWords.hide()

    private val loading = BehaviorRelay.createDefault<Boolean>(false)
    val loadingState: Observable<Boolean> = loading.hide()

    val searchButtonEnabledState: Observable<Boolean> = loading.map { !it }.hide()
    // --------------

    // --- UI-events ---
    private val searchQuery = PublishRelay.create<String>()
    val searchQueryConsumer: Consumer<String> = searchQuery

    private val inputTextChanges = PublishRelay.create<String>()
    val inputTextChangesConsumer: Consumer<String> = inputTextChanges

    private val searchButtonClicks = PublishRelay.create<Unit>()
    val searchButtonClicksConsumer: Consumer<Unit> = searchButtonClicks

    // ---------------

    private var disposable: Disposable? = null

    fun onCreate() {

        val filteredText = inputTextChanges.filter(String::isNotEmpty)
        val filteredQuery = searchQuery.filter(String::isNotEmpty)

        val combine = Observable.combineLatest(filteredText, filteredQuery, BiFunction(::SearchParams))

        val requestByClick = searchButtonClicks.withLatestFrom(combine,
                BiFunction<Unit, SearchParams, SearchParams> { _, params: SearchParams -> params })

        disposable = requestByClick
                .filter { !isLoading() }
                .doOnNext { showProgress() }
                .delay(3, TimeUnit.SECONDS) // делаем задержку чтобу увидеть прогресс
                .flatMap { interactor.findWords(it).toObservable() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach { hideProgress() }
                .subscribe(foundWords)
    }

    fun onDestroy() {
        disposable?.dispose()
    }

    private fun isLoading() = loading.value
    private fun showProgress() = loading.accept(true)
    private fun hideProgress() = loading.accept(false)
}