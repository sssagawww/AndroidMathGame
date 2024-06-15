# Quenta
**❗ Презентация, видео, apk и zip сервера для игры - в каталоге Presentation в репозитории ❗**

Сервер также в репозитории - https://github.com/sssagawww/amg_server (все мини-игры рассчитаны на 2 игроков, поэтому к серверу долнжо подключиться не более 2 человек)

### Обозначения

📁 - имя каталога
___
### Предложения по изменению

Размер экрана криво меняется, скорее всего из-за использования Tiled map. Они имеют фиксированный размер, и непонятно, могут ли растягиваться.

## Краткое описание работы
`AndroidLauncher` и `DesktopLauncher` грузятся независимо от кода, поэтому их можно менять в `Run configurations`.
 
Лаунчеры запускают `MyGdxGame`, который является стартовым классом, контролирующим Application. 
Здесь все методы, свойственные приложению ([про стартовые классы](https://libgdx.com/wiki/app/starter-classes-and-configuration)).

В `MyGdxGame` создаётся `GameStateManager`.
Тут в стэк помещаются states и вызываются методы верхнего элемента стэка. Изначально помещается `Menu2`.

Каждый state – наследник abstract `GameState`, поэтому должен реализовывать самые основные методы.
В `update` и `render` вызываются все update(), render() других классов, которые используются в данном state.
Тут обновляются и отрисовываются все элементы. В `GameState` есть MGG, 
из которого берётся камера, `SpriteBatch` ([про SpriteBatch](https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites)) и др.

## Классы
**MyGdxGame (MGG)** – стартовый класс, он начинает и поддерживает render и update, тут создаются камера, сохранение, GSM, скин, загружаются ресурсы (assets) и т.п.

**📁Battle:** все классы для механики с математикой

**📁DB:**

- `DBWrapper` - интерфейс для использования Sugar Orm и сохранения прогресса
- `Progress` - класс, содержащий то, что сохраняется

**📁Dialog:** классы для реализации диалогов и текста

**📁Entities:**

- `B2DSprite` – родительский класс для всех сущностей (игрок, враг), имеет базовые для них поля – x, y, скорость, 
направление движения, body (из Box2D). Обновляет анимацию.

	[Всё про библиотеку Box2D](https://libgdx.com/wiki/extensions/physics/box2d)

- `BattleEntity` – класс для врага в бое с матетматикой
- `MovableNPC` – npc, который может двигаться и реагировать на столкновения
- `PlayEntities` – класс, содержащий лист с npc, которые стоят на месте и не могут двигаться, но реагируют на коллизию
- `Player2` – игрок, двигается и меняет анимацию при разных направлениях, звуки топания
- `SlimeBoss` – финальный босс и его анимации
- `StaticNPC` – npc, которые не могут двигаться и не обладают коллизией

**📁Handlers:**

- `Animation` – простая анимация, связанная с прокручиванием регионов текстуры
- `B2DVars` – финальные переменные для использования в объектах
- `BoundedCamera` – камера с границей (когда игрок подходит к краю карты, камера останавливается)
- `Content` – был до `SkinManager`, загружает текстуры
- `Controllable` – интерфейс для стейтов, где моэно передвигаться
- **GameStateManager (GSM)** – основной класс для работы игры, меняет стейты, обновляет и отрисовывает эти стейты
- `MyContactListener` – обрабатывает контакт коллизии
- `SkinManager` – создаёт скин (класс LibGdx), загружая в него регионы с атласов 
(файл .atlas, хранит картинки по размерам и координатам с названием такой области)

**📁Multiplayer:**

- `MushroomsRequest` – класс с запросами к серверу

**📁Paint:** классы для рисования фигур (сами фигуры, алгоритм сравнения)

**📁States:**

- **GameState** – абстрактный класс для реализации стейтов. В нём то, что должно быть для игры – камера, `SpriteBatch`, 
обновление, рендер и т.д.
- `BattleState2` - режим с математическим боем
- `BlackScreen` - чёрный экран с титрами в начале и конце игры
- `BossFightState` - локация с финальным боссом
- `DungeonState` - локация с подземельем
- `Forest` - локация с лесом
- `MazeState` - локация с руинами и лабиринтом
- `Menu2` - меню со всеми кнопками
- `MushroomsState` - локация для многопользовательской игры (МП), где нужно собирать грибы
- `PaintState` - режим с рисованием фигур
- `Play` - основная локация, где появляется игрок
- `RhythmState` - режим с вытягиванием меча/удара мечом босса (механика - проверка на реакцию)

**📁UI:** классы, используемые для пользовательского интерфейса игры

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
  
## Мультиплеер
В данной игре есть возможность игры по сети (❗вдвоем❗). 

**Инструкция по запуску**

1. Нажать на кнопку `Cетевая игра` в `Главном меню`
2. Ввести в  поле `IP сервера` "quentagame.ru:9000"
3. Если всё хорошо, то айпи останется в поле ввода, иначе - сотрётся, и его нужно будет ввести ещё раз и нажать снова на галочку
4. Далее можно выбрать режим
5. В самом режиме нужно нажать на кнопку готовности, дождаться 2 игрока и начать игру

