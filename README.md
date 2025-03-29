# Warcraft III Translations

wc3trans is a tool which can be used to update target war3map.wts files from a single source war3map.wts file.
For example, if your map contains an English war3map.wts file like this:

```
STRING 0
// New comment
{
Same text.
}

STRING 1
// New entry
{
New text.
}

```

and it contains a German one in the sub folder _Locales\deDE.w3mod:

```
STRING 0
// Old comment
{
Gleicher Text.
}

STRING 2
// Old entry
{
Alter text.
}

```

which is outdated by now, you can use this tool to update it:

```
java -jar wc3trans.jar war3map.wts _Locales\deDE.w3mod\war3map.wts
```

It will automatically delete the old entry, add the new entry and update all comments resulting in

```
STRING 0
// New comment
{
Gleicher Text.
}

STRING 1
// New entry
{
New entry.
}

```

Obviously you still have to translate all English texts.

You can use multiple target files at once:

```
java -jar wc3trans.jar war3map.wts _Locales\deDE.w3mod\war3map.wts _Locales\frFR.w3mod\war3map.wts _Locales\plPL.w3mod\war3map.wts
```

## Build

Create a JAR with dependencies:

```bash
mvn clean compile assembly:single
```