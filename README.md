# Class-Serializer

Serialize and deserialize classes into maps.

## Add maven dependency

```xml
<repositories>
    <id>2lstudios-repo</id>
    <url>https://ci.2lstudios.dev/plugin/repository/everything</url>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.dotphin</groupId>
        <artifactId>Class-Serializer</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>
```

## Creating our Entity

We can create our entity by decorating it with the @Serializable annotation, in this way all the fields (public and inherited) of the class will be taken into account.
```java
@Serializable
class User {
    public String name;
    public int age;
}
```

We can also define which ones we want to be serialized and which ones we don't, instead of placing the @Serializable annotation in the class we can add the @Prop annotation in the properties that we want to take into account.
```java
class User {
    @Prop
    public String name;
    @Prop
    public int age;
}
```

## Serialize entity to map

We can convert objects with classes to maps and pass all their properties to map entries.

```java
User user = new User();
user.name = "Sammwy";
user.age = 20;

ClassSerializer serializer = new ClassSerializer();
Map<String, Object> values = serializer.serialize(user);

System.out.println((String) values.get("name")); // This will print "Sammwy".
```

## Deserialize map to entity

We can also do the opposite, pass the entries from a map to a class/object.

```java
ClassSerializer serializer = new ClassSerializer();
Map<String, Object> values = new HashMap();

values.put("name", "Sammwy");
values.put("age", 20);

User user = serializer.deserialize(User.class, values);
System.out.println(user.name); // This will print "Sammwy".
```