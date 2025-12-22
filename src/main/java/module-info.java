module org.R4ZXRN3T.firstpass {
	// JDK modules
	requires java.base;
	requires java.desktop;  // Swing, AWT

	// Third-party libraries
	requires org.json;
	requires com.formdev.flatlaf;
	requires jasypt;
	requires java.compiler;

	exports org.R4ZXRN3T.firstpass;
}