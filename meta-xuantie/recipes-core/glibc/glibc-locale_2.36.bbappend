# Filter SUPPORTED file to only include locales that have definition files available.
# This fixes the mismatch between external toolchain's SUPPORTED list (glibc-2.42)
# and actual locale definition files (subset from older glibc).
#
# IMPORTANT: Must run in do_install:append (before do_package), targeting ${WORKDIR}/SUPPORTED,
# because package_do_split_gconvs reads ${WORKDIR}/SUPPORTED at the very beginning of do_package.
# Filtering locale-tree/SUPPORTED or overriding do_prep_locale_tree is too late and ineffective.
#
# Only exact locale name match is used (no base_locale fallback):
#   aa_ER@saaho requires locales/aa_ER@saaho to exist; falling back to aa_ER is wrong
#   because cross-localedef specifically opens the @variant file.
do_install:append() {
    local supported="${WORKDIR}/SUPPORTED"
    local locales_dir="${D}${datadir}/i18n/locales"
    local tmp_supported="${supported}.filtered"

    if [ ! -f "${supported}" ]; then
        return
    fi

    rm -f "$tmp_supported"
    while IFS= read -r line; do
        # Extract locale name (first field, before space).
        # glibc 2.42 SUPPORTED uses 'en_US.UTF-8 UTF-8' format, but the locale
        # definition file is named 'en_US' (without the .charset suffix).
        # @variant suffix (e.g. @saaho) is kept as-is because localedef
        # requires the exact 'aa_ER@saaho' file when @variant is present.
        locale_name=$(echo "$line" | awk '{print $1}')
        # Strip .charset suffix (e.g. .UTF-8, .ISO-8859-1) but preserve @variant
        locale_file=$(echo "$locale_name" | sed 's/\.[^@]*//')
        if [ -f "${locales_dir}/${locale_file}" ]; then
            echo "$line" >> "$tmp_supported"
        fi
    done < "${supported}"

    if [ -f "$tmp_supported" ]; then
        mv "$tmp_supported" "${supported}"
    fi
}
