# Кастомная реализация RxJava

## Функционал

 - Observer
    - метод `onNext()`
    - метод `onError()`
    - метод `onComplete()`
 - Observable - источник данных
    - метод `create()`
    - метод `just()`
 - Schedulers
    - ComputationScheduler - `fixedThreadPool`
    - IOThreadScheduler - `cachedThreadPool`
    - SingleScheduler - `singleThreadPool`
 - Disposable/CompositeDisposable
    - Disposable - одиночная отмена подписки
    - CompositeDisposable - групповая отмена подписки
 - Operators
    - MapOperator - `map`
    - FilterOperator - `filter`
    - FlatMapOperator - `flatMap`

## Принципы работы Schedulers

IOThreadScheduler - I/O-bound задачи

ComputationScheduler - CPU-bound задачи

SingleScheduler - последовательное выполнение

### Результаты тестирования

Файл [Main.java](https://github.com/soldey/cw-rx-java/blob/master/src/main/Main.java) демонстрирует работу основного функционала приложения на двух сценариях:
1. Использование Map и Filter (сначала Map, затем Filter)
2. Использование FlatMap

Помимо демонстрации их работы, в пакете [test](https://github.com/soldey/cw-rx-java/tree/master/src/test) также есть Unit тесты для тестирования основного функционала:
 - Тестирование Observable (подписки)
 - Тестирование Operator (операторы)
 - Тестирование Scheduler (IOThreadScheduler и SingleScheduler)

### Пример использования

1. Создание Observable
```java
Observable<Integer> source = Observable.just(1, 2, 3, 4, 5);
```

2. Использование операторов
```java
MapOperator.apply(source, i -> i * 5)
```

3. Использование планировщиков
```java
// Подписка в I/O потоке и наблюдение в вычислительном потоке
MapOperator.apply(source, i -> i * 5)
        .subscribeOn(new IOThreadScheduler())
        .observeOn(new ComputationScheduler())
```

4. Подписка
```java
source.subscribe(
        item -> ..., // onNext()
        error -> ..., // onError()
        () -> ... // onComplete()
);
source.subscribe(item -> ...); // только onNext()
```
