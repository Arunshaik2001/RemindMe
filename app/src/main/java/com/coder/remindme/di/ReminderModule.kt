package com.coder.remindme.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.coder.core.util.Constants
import com.coder.remindme.data.local.converter.InstantConverter
import com.coder.remindme.data.local.dao.ReminderDao
import com.coder.remindme.data.local.database.ReminderDatabase
import com.coder.remindme.data.repository.ReminderRepositoryImpl
import com.coder.remindme.domain.repository.ReminderRepository
import com.coder.remindme.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderModule {


    @Provides
    @Singleton
    fun provideReminderDatabase(app: Application): ReminderDatabase {
        return Room.databaseBuilder(
            app, ReminderDatabase::class.java, "reminder_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideReminderRepository(
        db: ReminderDatabase,
        @ApplicationContext appContext: Context
    ): ReminderRepository {
        return ReminderRepositoryImpl(db.reminderDao, appContext)
    }

    @Provides
    @Singleton
    fun provideInsertReminderUseCase(reminderRepo: ReminderRepository): InsertReminderUseCase {
        return InsertReminderUseCase(reminderRepo)
    }

    @Provides
    @Singleton
    fun provideDeleteReminderUseCase(reminderRepo: ReminderRepository): DeleteReminderUseCase {
        return DeleteReminderUseCase(reminderRepo)
    }

    @Provides
    @Singleton
    fun provideGetReminderByIdUseCase(reminderRepo: ReminderRepository): GetReminderByIdUseCase {
        return GetReminderByIdUseCase(reminderRepo)
    }

    @Provides
    @Singleton
    fun provideUpdateReminderUseCase(reminderRepo: ReminderRepository): UpdateReminderUseCase {
        return UpdateReminderUseCase(reminderRepo)
    }

    @Provides
    @Singleton
    fun provideGetRemindersUseCase(reminderRepo: ReminderRepository): GetRemindersUseCase {
        return GetRemindersUseCase(reminderRepo)
    }

    @Provides
    @Singleton
    fun provideReminderDao(db: ReminderDatabase): ReminderDao = db.reminderDao


    @Provides
    @Singleton
    fun provideReminderUseCases(
        insertReminderUseCase: InsertReminderUseCase,
        deleteReminderUseCase: DeleteReminderUseCase,
        getReminderByIdUseCase: GetReminderByIdUseCase,
        updateReminderUseCase: UpdateReminderUseCase,
        getRemindersUseCase: GetRemindersUseCase,
    ): ReminderUseCases {
        return ReminderUseCases(
            insertReminderUseCase = insertReminderUseCase,
            deleteReminderUseCase = deleteReminderUseCase,
            getReminderByIdUseCase = getReminderByIdUseCase,
            updateReminderUseCase = updateReminderUseCase,
            getRemindersUseCase = getRemindersUseCase
        )
    }
}