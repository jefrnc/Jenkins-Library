# Jenkins Shared Library

A collection of reusable Groovy pipeline steps for Jenkins. These shared library functions encapsulate common CI/CD operations — Docker builds, ECS deployments, Maven lifecycle, database migrations, artifact management, and more.

## Available Functions

### Docker & Containers
| Function | Description |
|---|---|
| `buildImage` | Build Docker images from Dockerfile |
| `deployImage` | Deploy Docker images to target environment |
| `tagImage` | Tag Docker images for registry push |
| `dockero` | Docker helper operations |

### AWS & ECS
| Function | Description |
|---|---|
| `ecs` | ECS service deployment and management |
| `awso` | AWS CLI operations wrapper |
| `codeartifact` | AWS CodeArtifact integration |

### Build & Artifacts
| Function | Description |
|---|---|
| `maven` | Maven build lifecycle (compile, test, package) |
| `getNexusVersions` | Query Nexus for available artifact versions |
| `getNexusVersions2` | Extended Nexus version queries |
| `flyway` | Flyway database migration execution |

### Git & SCM
| Function | Description |
|---|---|
| `gitClone` | Clone repositories with credentials |
| `githelper` | Git utility operations (tagging, branching) |
| `selectTag` | Interactive tag selection for deployments |

### Pipeline Utilities
| Function | Description |
|---|---|
| `defaultContext` | Set default pipeline context and variables |
| `getProject` | Extract project metadata |
| `getTargetEnvFromEcosystem` | Resolve target environment from ecosystem config |
| `getTargetEnvPortFromEcosystem` | Resolve target port from ecosystem config |
| `processApproval` | Manual approval gates |
| `utils` | General-purpose utilities |

### Configuration Management
| Function | Description |
|---|---|
| `applyConfiguration` | Apply environment-specific configurations |
| `applyTemplate` | Process and apply templates |
| `processConfiguration` | Parse and process config files |
| `processTemplate` | Template rendering |

### Integrations
| Function | Description |
|---|---|
| `jira` | Jira issue tracking operations |
| `assertPluginsInstalled` | Verify required Jenkins plugins |
| `restartApplication` | Application restart with health checks |

## Usage

### Global Configuration (Jenkins-wide)

Go to **Manage Jenkins > Configure System > Global Pipeline Libraries** and add:

- **Name:** `my-shared-lib`
- **Default version:** `main`
- **Source:** Git — `https://github.com/jefrnc/Jenkins-Library.git`

### Per-Pipeline

```groovy
@Library('my-shared-lib') _

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                buildImage(name: 'my-app', tag: env.BUILD_NUMBER)
            }
        }
        stage('Deploy') {
            steps {
                ecs(cluster: 'production', service: 'my-app')
            }
        }
    }
}
```

### Single Function Import

```groovy
@Library('my-shared-lib') import buildImage

buildImage(name: 'my-app', tag: 'latest')
```

## References

- [Jenkins Shared Libraries Documentation](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
- [Pipeline Syntax Reference](https://www.jenkins.io/doc/book/pipeline/syntax/)

## License

MIT
