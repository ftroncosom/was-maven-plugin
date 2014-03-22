package com.orctom.mojo.was;

import com.orctom.mojo.was.model.WebSphereModel;
import com.orctom.mojo.was.service.WebSphereServiceFactory;
import com.orctom.mojo.was.utils.AntTaskUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.IOException;
import java.util.Set;

/**
 * websphere deployment mojo
 * Created by CH on 3/4/14.
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, requiresDirectInvocation = true, threadSafe = true)
public class WASDeployMojo extends AbstractWASMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(Constants.PLUGIN_ID + " - deploy");
        Set<WebSphereModel> models = getWebSphereModels();

        if (models.isEmpty()) {
            getLog().info("[SKIPPED] empty target server configured, please check your configuration");
            return;
        }
        String workingDir = project.getBuild().getOutputDirectory();

        for (WebSphereModel model : models) {
            getLog().info("============================================================");
            getLog().info("[DEPLOY] " + model.getHost() + " " + model.getApplicationName());
            getLog().info("============================================================");
            System.out.println(model);
            executeAntTasks(model, super.preSteps);
            WebSphereServiceFactory.getService(mode, model, workingDir).deploy();
            executeAntTasks(model, super.postSteps);
        }
    }

    private void executeAntTasks(WebSphereModel model, PlexusConfiguration[] targets) {
        for (PlexusConfiguration target : targets) {
            try {
                AntTaskUtils.execute(model, target, project, projectHelper, pluginArtifacts, getLog());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MojoExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}