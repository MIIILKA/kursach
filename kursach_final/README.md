# MyEditor Pro — Графічний редактор

## Як запустити в IntelliJ IDEA
1. Відкрити папку `kursach_final` як проект
2. File → Project Structure → Modules → Sources → позначити `src` як Sources Root
3. Натиснути **Run** або Shift+F10

## Як зібрати JAR вручну
```
javac -d out src/ua/lpnu/editor/*.java
jar cfe myeditor.jar ua.lpnu.editor.Main -C out .
java -jar myeditor.jar
```

## Гарячі клавіші
- Ctrl+Z / Ctrl+Y — Undo / Redo
- Ctrl+S — Зберегти як PNG або JPEG
- Ctrl+V — Вставити зображення з буфера обміну
- Delete — Видалити вибрану фігуру
- G — Сітка
- Shift (при малюванні) — квадрат / коло рівних сторін

## Що реалізовано
- 10 інструментів: Select, Rectangle, Circle, Triangle, Line, Arrow, Pencil, Eraser, Text, Picker
- Undo / Redo (до 50 кроків)
- Збереження в PNG та JPEG
- Відкриття зображень з файлу
- Вставка з буфера обміну (Ctrl+V)
- Переміщення фігур (інструмент Select)
- Сітка (G)
- Shift — пропорційне малювання
- 5 шаблонів: Візитка, Постер, Блок-схема, Логотип, Рамка
- Меню: Файл / Правка / Вид / Шаблони / Допомога
