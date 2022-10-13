### Описание  
Плагин реализует функции avg, max и values.  
Является измененной версий плагина из статьи [My first steps in OpenSearch Plugins](https://opensearch.org/blog/technical-posts/2021/06/my-first-steps-in-opensearch-plugins/)  
Функции Avg и Max реализованы внутри плагина, а функция Values сделана через TermsAggregation  

### Сборка  
Для сборки нужно открыть проект в IntelliJ IDEA. Запустить Build\Build Project  
Затем запустить Gradle task "check".  
В папке build\discriptions будет файл opensearch-rest-aggregator-plugin.zip - это плагин  

### Установка  
Для установки нужно cкопировать zip архив с плагином на ноду OpenSearch или выложить в сеть  
чтобы файл был доступен по http.  
Если копирование происходит в docker то команда для копирования будет такой  
```bash
docker cp opensearch-rest-aggregator-plugin.zip containerid:/usr/share/opensearch/
```

Удаление старой версии плагина и установка новой (это нужно выполнить внутри docker или на ноде OpenSearch).
```bash
./bin/opensearch-plugin remove opensearch-rest-aggregator-plugin
./bin/opensearch-plugin install file:///usr/share/opensearch/opensearch-rest-aggregator-plugin.zip
```
После установки перезапустить OpenSearch

### Запуск  
Для проверки функций можно воскользоваться Postman или curl.  
Тестировалось только на данных индекса opensearch_dashboards_sample_data_ecommerce.  
В целом нужно отправить POST запрос на url
https://localhost:9200/_plugins/hello_world  
Content-Type: applicaton/json  
В тело запроса нужно поместить параметры  
```json
{
    "functionName": "values",
    "indexName": "opensearch_dashboards_sample_data_ecommerce",
    "fieldName": "currency"
}
```  
Внимание! Функция values может выводить только первые 10000 уникальных значений.  
