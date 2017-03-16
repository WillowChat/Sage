# Sage

@CarrotCodes' personal IRC bot. Uses [Warren](https://github.com/WillowChat/Warren) for all the IRC bits.

I don't intend to support this for other people, and it's configured specifically for me, but it's liberally licensed so do what you want with it. 

## Support

<a href="https://patreon.com/carrotcodes"><img src="https://s3.amazonaws.com/patreon_public_assets/toolbox/patreon.png" align="left" width="160" ></a>
If you use this library and you'd like to support my open-source work, please consider tipping through [Patreon](https://patreon.com/carrotcodes).

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).

## Building
This project uses Gradle for pretty easy setup and building.

* **Setup**: `./gradlew clean`
* **Building**: `./gradlew clean build` - this will also produce a fat Jar with shaded dependencies included