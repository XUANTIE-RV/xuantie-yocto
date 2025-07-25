import os
import sys
import subprocess

def toolchain_init():
    main_script_path = sys.argv[0]
    main_script_dir = os.path.dirname(os.path.realpath(main_script_path))

    main_dir = os.path.abspath(os.path.join(main_script_dir, "../../.."))
    script_path = os.path.join(
        main_dir,
        "meta-xuantie",
        "conf",
        "distro",
        "include",
        "xuantie-toolchain-init.sh"
    )

    if not os.path.exists(script_path):
        sys.exit(f"Error: The toolchain script {script_math} does not exist!")

    try:
        subprocess.run([script_path], check=True)
    except subprocess.CalledProcessError as e:
        sys.exit(f"Toolchain initialization failed:{str(e)}")
