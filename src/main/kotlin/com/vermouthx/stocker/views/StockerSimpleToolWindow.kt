package com.vermouthx.stocker.views

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.vermouthx.stocker.actions.StockerRefreshAction
import com.vermouthx.stocker.actions.StockerStockAddAction
import com.vermouthx.stocker.actions.StockerStockDeleteAction
import com.vermouthx.stocker.actions.StockerStopAction

class StockerSimpleToolWindow : SimpleToolWindowPanel(true) {
    var tableView: StockerTableView = StockerTableView()

    init {
        val actionManager = ActionManager.getInstance()
        val actionGroup =
            DefaultActionGroup(
                listOf(
                    StockerRefreshAction::class.qualifiedName?.let { actionManager.getAction(it) },
                    StockerStopAction::class.qualifiedName?.let { actionManager.getAction(it) },
                    StockerStockAddAction::class.qualifiedName?.let { actionManager.getAction(it) },
                    StockerStockDeleteAction::class.qualifiedName?.let { actionManager.getAction(it) }
                )
            )
        val actionToolbar = actionManager.createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, actionGroup, true)
        this.toolbar = actionToolbar.component
        setContent(tableView.component)
    }
}