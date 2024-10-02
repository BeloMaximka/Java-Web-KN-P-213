package itstep.learning.services.kdf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.hash.HashService;

@Singleton
public class PbKdf1Service implements KdfService {
    private final HashService hashService;

    @Inject
    public PbKdf1Service(HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public String dk(String password, String salt) {
        int iterationCount = getIterationCount((salt));

        String t = hashService.hash(password + salt);
        for (int i = 1; i <= iterationCount; i++) {
            t = hashService.hash(t + salt);
        }
        int dkLen = 20;
        while(t.length() < dkLen) {
            t += t;
        }

        return t.substring(0, dkLen);
    }

    private int getIterationCount(String salt) {
        int iterationCount = 0;
        int dotPos = salt.indexOf('.');
        if (dotPos > 0) {
            try {
                iterationCount = Integer.parseInt(salt.substring(dotPos + 1));
            }
            catch (NumberFormatException ignored) {}
        }
        if(iterationCount < 0 || iterationCount > 10) {
            iterationCount = 3;
        }
        return iterationCount;
    }
}
