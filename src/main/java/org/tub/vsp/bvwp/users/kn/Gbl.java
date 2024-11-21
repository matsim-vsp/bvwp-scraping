package org.tub.vsp.bvwp.users.kn;

class Gbl {
	private Gbl(){} // do not instantiate

	public static final void assertTrue( boolean condition ) {
		assertTrue( condition, "assertion failed; look into code to find reasin" );
	}
	public static final void assertTrue( boolean condition, String msg ) {
		if ( !condition ) {
			throw new RuntimeException( msg );
		}
	}

}
