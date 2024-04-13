# MathGame
Описание проекта, его классов и идеи по улучшению.
### Обозначения
🟢 - является особенностью libGdx или других движков, если не отмечено таким значком,
то это своя реализация, которую можно изменить, она не должна быть обязательно такой

📁 - directory name
___
### Предложения по изменению
Все взаимодействия сделаны через клавиатуру, а части UI реализованы как таблицы с содержимым (`Image`, `Label`, др). 
Скорее всего, проще будет сделать обычными `ImageButton`/`TextButton`/др. 
Возможно, пропадут элементы игры по типу стрелочек в `OptionBox`, но будет легче реализация(?).
Тогда можно будет кликать прям на них или передавать сигнал в контроллер и другие классы.

Размер экрана не меняется, скорее всего из-за использования Tiled map. Они имеют фиксированный размер, и непонятно, могут ли растягиваться. Нужно ли заменить их везде, где они не являются игровой мапой?

## Краткое описание работы
`AndroidLauncher` и `DesktopLauncher` грузятся независимо от кода, поэтому их можно менять в `Run configurations`
 
🟢Лаунчеры запускают `MyGdxGame`, который является стартовым классом, контролирующим Application. 
Здесь все методы, свойственные приложению ([про стартовые классы](https://libgdx.com/wiki/app/starter-classes-and-configuration)).

В `MyGdxGame` создаётся `GameStateManager`.
Тут в стэк помещаются states и вызываются методы верхнего элемента стэка. Изначально помещается `Menu2`.

Каждый state – наследник abstract `GameState`, поэтому должен реализовывать самые основные методы `update`, `render`. 
HandleInput, dispose и не добавленный resize по идее должны использоваться, но им не уделено внимания.
В `update` и `render` вызываются все update, render других классов, которые используются в данном state.
Тут обновляются и отрисовываются все элементы, поэтому если вдруг что-то не показывается или не изменяет состояние,
и это что-то должно иметь эти 2 метода, то скорее всего их не вызвали. В `GameState` есть MGG, 
из которого берётся камера, `SpriteBatch` ([про SpriteBatch](https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites)).

## Classes
**MyGdxGame (MGG)** – стартовый класс, он начинает и поддерживает render и update, тут создаются камера, сохранение, GSM, скин, загружаются ресурсы (assets).

**📁Data:**

- `DataStorage` – класс с переменными, которые нужно сохранить
- `SaveLoad` – сохраняет позицию в файл save.dat

**📁Entities:**

- `B2DSprite` – родительский класс для всех сущностей (игрок, враг), имеет базовые для них поля – x, y, скорость, 
направление движения, body (из Box2D). Обновляет анимацию

	[Всё про библиотеку Box2D](https://libgdx.com/wiki/extensions/physics/box2d)

- `Boss` – класс для врага, сейчас просто стоит на месте и прокручивает анимацию
- `Player2` (2 потому что было 2 варианта, и этот оказался лучше) – игрок, двигается и меняет анимацию при разных направлениях
 
**📁States:**

- **GameState** – абстрактный класс для реализации стейтов. В нём то, что должно быть для игры – камера, `SpriteBatch`, 
обновление, рендер. Меню, основная локация, где бегает перс, локация с боем – это всё стейты.
- `Play` - основная локация
- `Menu2` - меню
- `BattleState` - локация с боем

**📁Handlers:**

- `Animation` – просто прокручивает регионы текстуры. Для более сложной анимации, наверное, нужен новый класс
- `B2DVars` – final переменные для использования в объектах
- `BoundedCamera` – камера с границей, чтобы когда игрок подходил к краю карты, камера останавливалась
- `Content` – был до `SkinManager`, загружает текстуры. Более удобно, наверное, будет использовать скины вместо него
- **GameStateManager (GSM)** – основной класс для работы игры, меняет стейты, update и render эти стейты через стартовый MGG
- `MyContactListener` – обрабатывает контакт коллизии
- `SkinManager` – создаёт скин (класс LibGdx), загружая в него регионы с атласов 
(файл .atlas, хранит картинки по размерам и координатам с названием такой области)

**📁UI:**

- `Controller` – контроллер на экране; наследуется от `Table`; все картинки (которые выполняют роль кнопок) помещаются в таблицу, выравниваются.
- `DialogBox` – анимация вывода текста по буквам
- `HP_Bar` – картинка «HP» и полоска с хп
- `MenuButton2` – тестовый вариант сделать кнопку как нормальную кнопку, а не картинку/label.
- `MenuOptionBox` – аналогично `OptionBox`, но каждый вариант выбора отделён от другого, у него свой фон.
- `OptionBox` – варианты ответа в столбик (как в меню), все на одном фоне.
- `PlayerStatusBox` – наследник `StatusBox`, должен был использоваться для показа количества хп ещё и в цифрах, а не просто полоской. Сейчас он просто `StatusBox`.
- `SelectionBox` – элемент с выбором ответа в примерах
- `StatusBox` – имя и хп энтити

## Описание работы алгоритма сравнения нарисованных фигур
Фигуры в данной игре представляют собой набор точек, имеющих свои координаты. 

**Масштабирование**

Рисунок `X` на 	`Y` пикселей сжимается до размеров `1х1` следующим образом:
- Находим `ширину` фигуры. Для этого ищем точки с наименьшим и с наибольшим `X`. `Ширина` = `Х`max - `Х`min
- Аналогично вычислим ее `высоту`
- Определим некоторую переменную `S`, равную набольшему из `ширины` и `высоты` значению
- Для каждой точки заменить координату `X`  на `(X - Xmin) / S`
- Аналогично поступить с `Y`

**Сравнение**

В начале было сказано, что фигуры представлены, как массивы точек. Назовем их `Points1` и `Points2`
- Теперь для кождой точки из `Points1` найдем ближайшую точку в `Points2`. Расстояние вычисляем по формуле  $D = \sqrt{(x_1 - x_2)^2 + (y_1 - y_2)^2}$.
- Для каждой точки мы нашли ближайшую и измерили расстояние между ними. Из массива расстояний находим максимальное значение
  и сравниваем его с `максимально допустимым порогом`
- Если `порог` меньше, то фигуры различаются, иначе - совпадают

**Определение порога**

Для определения порога было создано [мини-приложение](https://github.com/mearlixxx/DrawApp).
| Фигуры совпадают  | Фигуры различаются   |
|---|---|
| ![Screenshot1](https://github.com/sssagawww/AndroidMathGame/assets/116021916/040b09d0-443f-4ea2-98a2-d6b1b960fc18)  |  ![Screenshot2](https://github.com/sssagawww/AndroidMathGame/assets/116021916/33f224c5-aaa5-4a12-86aa-fb395e1b08c8) |

Сверху есть `ползунок`, которым и регулируется `порог`
- При значениях `30-50` приложение почти в 100% случаев думает, что фигуры *совпадают*, даже если нарисованы разные
- При значениях `20 - 30` приложение ошибается редко. Но его слабость - пара `квадрат и круг`. Оно думает, что это одинаковые фигуры
- При значениях `<15` нарисовать так, чтобы приложение сказало, что фигуры совпадают, очень сложно.
- Идеальным значением `порога` стало `15`. Приложение отличает все фигуры, допуская максимальную погрешность,
  допущенную `игроком` во время рисования.
  

