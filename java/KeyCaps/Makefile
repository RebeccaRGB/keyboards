SRCFILES := $(shell find src -name \*.java)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc keycaps.jar

eclipseall: eclipseclean keycaps.jar

clean:
	rm -rf bin
	rm -rf doc
	rm -rf *.jar

eclipseclean:
	rm -rf *.jar

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -sourcepath src $(SRCFILES) -d bin

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -sourcepath src $(PACKAGES) -d doc

keycaps.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF keycaps.jar -C bin com/kreative/keycaps
	chmod +x keycaps.jar

.PHONY: all eclipseall clean eclipseclean
