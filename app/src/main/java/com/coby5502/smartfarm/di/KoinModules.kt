package com.coby5502.smartfarm.di

import com.coby5502.smartfarm.Repository
import com.coby5502.smartfarm.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module{
    single{
        Repository()
    }
}
