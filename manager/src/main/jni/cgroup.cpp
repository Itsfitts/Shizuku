#include <cstring>
#include <fcntl.h>
#include <unistd.h>
#include <cstdio>

namespace cgroup {
    bool switch_cgroup(const char *cgroup, int pid) {
        char path[1024], buf[8];
        snprintf(buf, sizeof(buf), "%d\n", pid);
        snprintf(path, sizeof(path), "%s/uid_0/cgroup.procs", cgroup);
        if (access(path, F_OK) != 0) {
            if (access(path, F_OK) != 0) {
                snprintf(path, sizeof(path), "%s/cgroup.procs", cgroup);
                return false;
            }
        }
        int fd = open(path, O_WRONLY);
        if (fd < 0)
            return false;

        if (write(fd, buf, strlen(buf)) == -1) {
            close(fd);
            return false;
        }
        close(fd);
        return true;
    }
}
