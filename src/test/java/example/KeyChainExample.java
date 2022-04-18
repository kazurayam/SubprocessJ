package example;

import com.kazurayam.subprocessj.Subprocess;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyChainExample {

    @Test
    public void test_macos_security_findinternetpassword()
            throws IOException, InterruptedException
    {
        Subprocess.CompletedProcess cp;
        cp = new Subprocess().cwd(new File("."))
                .run(Arrays.asList("security", "find-internet-password",
                        "-s", "katalon-demo-cura.herokuapp.com",
                        "-a", "John Doe",
                        "-w"));
        assertEquals("ThisIsNotAPassword", cp.stdout().get(0));
        System.out.println("password is '" + cp.stdout().get(0) + "'") ;
    }
}
