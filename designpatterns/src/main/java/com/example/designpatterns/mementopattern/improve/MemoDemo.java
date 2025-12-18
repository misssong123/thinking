package com.example.designpatterns.mementopattern.improve;

import java.util.List;

public class MemoDemo {
    public static void main(String[] args) {
        try{
            System.out.println("=== 增强版备忘录演示 ===\n");

            // 1. 初始化组件
            StateManager stateManager = new StateManager("/Users/mengsong/Downloads/test/demo_storage");
            HistoryManager historyManager = new HistoryManager(stateManager);
            DocumentEditor editor = new DocumentEditor();

            // 2. 创建初始文档
            editor.setTitle("Java备忘录模式文档");
            editor.appendContent("这是一个演示备忘录模式的文档。\n");
            editor.addTag("设计模式");
            editor.addTag("Java");
            editor.setProperty("author", "开发团队");

            // 3. 保存初始状态
            historyManager.saveState(editor, "初始状态");
            System.out.println("1. 初始状态已保存");
            printEditorState(editor);
            // 4. 进行编辑操作并保存
            editor.appendContent("备忘录模式是一种行为设计模式，允许在不破坏封装性的前提下捕获并外部化对象的内部状态。\n");
            historyManager.saveState(editor, "添加介绍");

            editor.appendContent("主要角色：Originator, Memento, Caretaker。\n");
            editor.addTag("行为模式");
            historyManager.saveState(editor, "添加角色说明");

            System.out.println("\n2. 进行了两次编辑操作");
            printEditorState(editor);

            // 5. 演示撤销操作
            System.out.println("\n3. 执行撤销操作...");
            if (historyManager.undo(editor)) {
                System.out.println("撤销成功，恢复到上一状态");
                printEditorState(editor);
            }

            // 6. 演示重做操作
            System.out.println("\n4. 执行重做操作...");
            if (historyManager.redo(editor)) {
                System.out.println("重做成功，恢复到最后状态");
                printEditorState(editor);
            }
            // 7. 创建分支
            String branchId = historyManager.createBranch("feature_branch");
            System.out.println("\n5. 创建分支，分支ID: " + branchId);

            // 8. 显示历史记录
            System.out.println("\n6. 历史记录:");
            List<EnhancedMemento> history = historyManager.getHistory();
            for (int i = 0; i < history.size(); i++) {
                EnhancedMemento memento = history.get(i);
                System.out.printf("  [%d] %s - %s%n",
                        i + 1,
                        memento.getTimestamp(),
                        memento.getDescription());
            }

            // 9. 演示从文件加载
            System.out.println("\n7. 从文件系统加载保存的备忘录:");
            List<EnhancedMemento> savedMementos = stateManager.listMementos();
            for (EnhancedMemento memento : savedMementos) {
                System.out.println("  - " + memento.getDescription() +
                        " (ID: " + memento.getId() + ")");
            }

            // 10. 清理测试文件
            stateManager.clearAll();
        }catch (Exception e){

        }
    }
    private static void printEditorState(DocumentEditor editor) {
        System.out.println(editor.getStateSummary());
        System.out.println("内容预览: " +
                (editor.getContent().length() > 100 ?
                        editor.getContent().substring(0, 100) + "..." :
                        editor.getContent()));
        System.out.println("标签: " + editor.getTags());
    }
}
