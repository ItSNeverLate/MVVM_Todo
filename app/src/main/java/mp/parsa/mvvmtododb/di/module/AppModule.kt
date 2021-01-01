package mp.parsa.mvvmtododb.di.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import mp.parsa.mvvmtododb.data.db.AppDataBase
import mp.parsa.mvvmtododb.di.annotation.AppScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDataBase(
        app: Application,
        callback: AppDataBase.Callback
    ) = Room.databaseBuilder(
        app,
        AppDataBase::class.java,
        "app_db"
    ).addCallback(callback)
        .fallbackToDestructiveMigration()
        .build()

    @AppScope
    @Singleton
    @Provides
    fun provideAppScope() = CoroutineScope(SupervisorJob())
}