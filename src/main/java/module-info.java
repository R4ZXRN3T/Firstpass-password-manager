module org.R4ZXRN3T.Firstpass {
	// JDK modules
	requires java.base;
	requires java.desktop;  // Swing, AWT

	// Third-party libraries
	requires org.json;
	requires com.formdev.flatlaf;
	requires jasypt;

	exports org.R4ZXRN3T;
}