# Перевірка артефактів UML-моделі

Перевірки виконуються з кореня репозиторію. Вони не додають залежностей до Java-застосунку; локальні PlantUML, Mermaid CLI та Python-бібліотеки є лише інструментами перевірки.

## Передумови

- Створено файли `docs/usecase.puml`, `docs/class.mmd`, `docs/sequence.mmd`, `docs/state.mmd` та `docs/crc.xlsx`.
- Для синтаксичної перевірки діаграм локально доступні Java з PlantUML JAR та Mermaid CLI (`mmdc`).
- Для програмного читання книги XLSX доступна Python-бібліотека `openpyxl`; альтернативно книгу можна відкрити в сумісному табличному застосунку.

## 1. Перевірити PlantUML

```sh
java -jar /шлях/до/plantuml.jar --check-syntax docs/usecase.puml
```

**Очікувано**: команда завершується без помилки синтаксису й не генерує зображення.

## 2. Перевірити Mermaid

```sh
mmdc -i docs/class.mmd -o /tmp/agrofarm-class.svg
mmdc -i docs/sequence.mmd -o /tmp/agrofarm-sequence.svg
mmdc -i docs/state.mmd -o /tmp/agrofarm-state.svg
```

**Очікувано**: усі три команди успішно розбирають та рендерять діаграми. SVG у `/tmp` є тимчасовими перевірочними файлами, не артефактами функції.

## 3. Перевірити книгу CRC

```sh
python3 - <<'PY'
from openpyxl import load_workbook

expected = {
    "FarmParticipant", "Agronomist", "Operator", "Storekeeper", "Field",
    "FieldWork", "CropYieldAnalysis", "WeatherService", "TechOperation",
}
workbook = load_workbook("docs/crc.xlsx", read_only=True, data_only=True)
assert set(workbook.sheetnames) == expected, workbook.sheetnames
for name in expected:
    sheet = workbook[name]
    assert sheet["A1"].value == name, (name, sheet["A1"].value)
    assert sheet["A2"].value == "обов'язки класу", (name, sheet["A2"].value)
    assert sheet["B2"].value == "з ким співпрацює", (name, sheet["B2"].value)
workbook.close()
print("CRC-книга: 9 карток перевірено")
PY
```

**Очікувано**: книга відкривається, містить рівно дев'ять аркушів-карток із правильними заголовками; друкується `CRC-книга: 9 карток перевірено`.

## 4. Перевірити узгодженість за специфікацією

Перевірити вручну вимоги FR-001–FR-015 та критерії SC-001–SC-007 у [специфікації](spec.md):

- усі три ролі пов'язані з бізнес-сценаріями; присутні чотири визначені сценарії;
- діаграма класів містить дев'ять класів та `IWorkPlan`, усі потрібні зв'язки, конструктори, `final`, перевизначення і перевантаження;
- кожне повідомлення послідовності відповідає операції класу, а типи об'єктів визначені на діаграмі класів;
- діаграма станів охоплює сезонний цикл від планування до обліку врожаю та має початковий і кінцевий стани;
- назви й етапи послідовні між файлами, CRC-картки відповідають класам, відсутні зовнішні погодні виклики та фізичні побічні ефекти.

**Очікувано**: чотири діаграми синтаксично коректні, структура книги відповідає моделі, а семантична перевірка не виявляє пропусків чи суперечностей.
