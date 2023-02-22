Параметры программы задаются при запуске через аргументы командной строки, по порядку:

1. режим сортировки (-a или -d), необязательный, по умолчанию сортируем по возрастанию;
2. тип данных (-s или -i), обязательный;
3. имя выходного файла, обязательное;
4. остальные параметры – имена входных файлов, не менее одного.
  
Примеры запуска из командной строки для Windows:

   sort-it.exe -i -a out.txt in.txt (для целых чисел по возрастанию) 

  sort-it.exe -s out.txt in1.txt in2.txt in3.txt (для строк по возрастанию)

  sort-it.exe -d -s out.txt in1.txt in2.txt (для строк по убыванию)
- версия Java : Oracle OpenJDK version 17.0.2
- система сборки : Maven version 3.8.1


- сторонние библиотеки :
1. org.apache.commons :

```
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

2. lombok

```
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.24</version>
    <scope>provided</scope>
</dependency>
```

3. commons io

```
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

4. logback

```
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.3.5</version>
</dependency>
```

- Сортировка слиянием. Особенности :
  - при указании неверного в соответствие с опциями ( строки при опции чисел или присутствия пробельных символов при опции строк ) данная строка пропускается
  - при нарушении порядка сортировки в исходном файле отсортирована будет только подпоследовательность, начинающаяся с первого валидного элемента файла, соответствующая заданному порядку ( если полностью обратный порядок - из файла берётся на сортировку только первый элемент )