package au.id.wolfe.bamboo.ruby.rvm;

import au.id.wolfe.bamboo.ruby.fixtures.RvmFixtures;
import au.id.wolfe.bamboo.ruby.rvm.util.FileSystemHelper;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static au.id.wolfe.bamboo.ruby.fixtures.RvmFixtures.getJRubyRuntimeDefaultGemSet;
import static au.id.wolfe.bamboo.ruby.fixtures.RvmFixtures.getMRIRubyRuntimeDefaultGemSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Ruby locator tests
 */
@RunWith(MockitoJUnitRunner.class)
public class RubyLocatorTest {

    @Mock
    FileSystemHelper fileSystemHelper;

    RubyLocator rubyLocator;

    @Before
    public void setUp() throws Exception {
        rubyLocator = new RubyLocator(fileSystemHelper, RvmFixtures.getUserRvmInstallation());
    }

    @Test
    public void testBuildEnv() throws Exception {

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when(fileSystemHelper.pathExists(mriRuby.getRubyExecutablePath())).thenReturn(true);
        when(fileSystemHelper.pathExists(mriRuby.getGemPath())).thenReturn(true);

        Map<String, String> envVars = rubyLocator.buildEnv("ruby-1.9.3-p0@default");

        assertTrue(envVars.containsKey(RvmUtil.MY_RUBY_HOME));
        assertTrue(envVars.containsKey(RvmUtil.GEM_HOME));
        assertTrue(envVars.containsKey(RvmUtil.GEM_PATH));
        assertTrue(envVars.containsKey(RvmUtil.BUNDLE_HOME));
        assertTrue(envVars.containsKey(RvmUtil.RVM_RUBY_STRING));
        assertTrue(envVars.containsKey(RvmUtil.RVM_GEM_SET));

    }

    @Test
    public void testGetRubyHome() throws Exception {

    }

    @Test
    public void testGetGemBinPath() throws Exception {

    }

    @Test
    public void testGetRubyRuntime() throws Exception {

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when(fileSystemHelper.pathExists(mriRuby.getRubyExecutablePath())).thenReturn(true);
        when(fileSystemHelper.pathExists(mriRuby.getGemPath())).thenReturn(true);

        RubyRuntime rubyRuntime;

        rubyRuntime = rubyLocator.getRubyRuntime("ruby-1.9.3-p0@default");

        assertEquals(mriRuby, rubyRuntime);

        final RubyRuntime jRuby = RvmFixtures.getJRubyRuntimeDefaultGemSet();

        when(fileSystemHelper.pathExists(jRuby.getRubyExecutablePath())).thenReturn(true);
        when(fileSystemHelper.pathExists(jRuby.getGemPath())).thenReturn(true);

        rubyRuntime = rubyLocator.getRubyRuntime("jruby-1.6.5@default");

        assertEquals(jRuby, rubyRuntime);

    }

    @Test
    public void testListRubyRuntimes() throws Exception {

        when(fileSystemHelper.listPathDirNames("/home/markw/.rvm/rubies")).thenReturn(
                Lists.newArrayList(
                        "jruby-1.6.5",
                        "ruby-1.9.3-p0")
        );

        when(fileSystemHelper.listPathDirNames("/home/markw/.rvm/gems")).thenReturn(
                Lists.newArrayList(
                        "jruby-1.6.5",
                        "jruby-1.6.5@global",
                        "ruby-1.9.3-p0",
                        "ruby-1.9.3-p0@global")
        );

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when(fileSystemHelper.pathExists(mriRuby.getRubyExecutablePath())).thenReturn(true);
        when(fileSystemHelper.pathExists(mriRuby.getGemPath())).thenReturn(true);

        final RubyRuntime jRuby = RvmFixtures.getJRubyRuntimeDefaultGemSet();

        when(fileSystemHelper.pathExists(jRuby.getRubyExecutablePath())).thenReturn(true);
        when(fileSystemHelper.pathExists(jRuby.getGemPath())).thenReturn(true);

        List<RubyRuntime> rubyRuntimeList = rubyLocator.listRubyRuntimes();

        assertEquals(2, rubyRuntimeList.size());

        assertTrue(rubyRuntimeList.contains(getMRIRubyRuntimeDefaultGemSet()));
        assertTrue(rubyRuntimeList.contains(getJRubyRuntimeDefaultGemSet()));


    }

    @Test
    public void testSearchForRubyExecutable(){

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when(fileSystemHelper.pathExists(mriRuby.getRubyExecutablePath())).thenReturn(true);
        when(fileSystemHelper.pathExists(mriRuby.getGemPath())).thenReturn(true);
        when(fileSystemHelper.executableFileExists(mriRuby.getGemPath() + "/bin/rake")).thenReturn(true);

        String executablePath = rubyLocator.searchForRubyExecutable("ruby-1.9.3-p0@default", "rake");

        assertEquals(mriRuby.getGemPath() + "/bin/rake", executablePath);

    }

    @Test
    public void testHasRuby() throws Exception {

        when(fileSystemHelper.listPathDirNames("/home/markw/.rvm/rubies")).thenReturn(
                Lists.newArrayList(
                        "jruby-1.6.5",
                        "ruby-1.9.3-p0")
        );

        assertTrue(rubyLocator.hasRuby("ruby-1.9.3-p0"));
        assertTrue(rubyLocator.hasRuby("jruby-1.6.5"));
    }
}