package mp.parsa.mvvmtododb.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import mp.parsa.mvvmtododb.data.db.AppDataBase

@Module
@InstallIn(ApplicationComponent::class)
object DataBaseModule {

    @Provides
    fun provideTaskDao(db: AppDataBase) = db.taskDao()
}