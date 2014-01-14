# Big-Bang [![Build Status](https://secure.travis-ci.org/rm-hull/big-bang.png)](http://travis-ci.org/rm-hull/big-bang)

ClojureScript game loop, loosely based on Racket's [big-bang][1] and implemented on top of _core.async_.

[1]: http://docs.racket-lang.org/teachpack/2htdpuniverse.html#(form._world._((lib._2htdp/universe..rkt)._big-bang))


See http://rm-hull.github.io/big-bang/example.html for some demos

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.3.4 or
above installed.

### Building & Testing

To build and install the library locally, run:

    $ lein cljsbuild once
    $ lein install

To test against [PhantomJS](http://phantomjs.org/), ensure that that
package is installed properly, and run:

    $ lein cljsbuild test

Alternatively, open ```resources/run-tests.html``` in a browser - this
executes the tests and the test results are displayed on the page.

### Including in your project

There _will be_ an 'alpha-quality' version hosted at [Clojars](https://clojars.org/rm-hull/big-bang) at some point soon.
For leiningen include a dependency:

```clojure
[rm-hull/big-bang "0.0.1-SNAPSHOT"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>big-bang</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```
## Basic Usage

## Differences from the Racket implementation

## TODO

* Variable :on-tick rate & re-org. recur/loop with animation-frame
* Implement :on-key and :on-release (maps to _keydown_ and _keyup_ events respectively)
* implement :on-mouse, add a (mouse-handler ...) like Racket's (pad-handler ...)
* Implement :on-touch with (touch-handler ...)
* Deregister event listeners on stop
* Return list of states on stop if :record? is true, else nil
* Playback states functionality via :playback
* External messages via :on-receive with :receive-channel & :send-channel
* ~~(make-package w m) & (package? x)~~
* Tests, documentation, examples

## Known Bugs

* ```lein cljsbuild test``` does not appear to be working properly, returns _"Could not locate test command ."_

## References

* http://docs.racket-lang.org/teachpack/2htdpuniverse.html#(form._world._((lib._2htdp/universe..rkt)._big-bang))

## License

The MIT License (MIT)

Copyright (c) 2014 Richard Hull

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/rm-hull/big-bang/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
