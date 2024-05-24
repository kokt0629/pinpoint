# build

## all
```
mvnw.cmd install -Dmaven.test.skip=true
```

## agent
```
mvnw.cmd install -pl agent -am -Dmaven.test.skip=true
```

## collector
```
mvnw.cmd install -pl collector -am -Dmaven.test.skip=true
```

## web
```
mvnw.cmd install -pl web -am -Dmaven.test.skip=true
```
Skip frontend build
```
mvnw.cmd install -pl web -am -Dbuild.frontend.skip=true -Dmaven.test.skip=true
```

