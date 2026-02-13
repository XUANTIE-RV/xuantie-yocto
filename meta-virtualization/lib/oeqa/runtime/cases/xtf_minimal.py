#
# SPDX-License-Identifier: MIT
#
# Author: Christopher Clark
# Copyright (c) Star Lab Corp, 2021
#
# Integration of the Xen Test Framework (XTF) into OpenEmbedded QA
#
# Since not all XTF test cases are appropriate for all test environments,
# images or machine configurations the selection of XTF test cases to run
# is determined by variables that can be set in an image recipe.
#
# * XTF_TEST_CASES_POPULATE
# Specifies the list of queries passed to xtf-runner to populate the test list.
# eg. 'pv64 livepatch-priv-check'
#
# Since the space character is meaningful and may be required within a populate clause,
# the '|' character is used for separating multiple queries.
# eg. 'pv64 livepatch-priv-check|pv32pae selftest'
#
# * XTF_TEST_CASES_SKIP
# A space-separate list of test cases that should be skipped even if returned
# from the queries specified in XTF_TEST_CASES_POPULATE.
# eg. 'test-pv64-livepatch-priv-check'
#
# * XTF_TEST_CASES_REQUIRE
# A space-separate list of test cases that must not be skipped even if missing
# from the results of the queries specified in XTF_TEST_CASES_POPULATE.

#----------
# The default single test case here is chosen because it exercises XTF
# and just Xen itself rather than any specifics of the hardware
# (virtual or not) that Xen is running on.
# TODO: this is an x86-specific test - revisit this choice when XTF supports Arm
DEFAULT_POPULATE = 'pv64 livepatch-priv-check'
#----------

import json
from oeqa.runtime.case import OERuntimeTestCase
from oeqa.core.decorator.depends import OETestDepends
from oeqa.core.decorator.oetimeout import OETimeout
from oeqa.runtime.decorator.package import OEHasPackage

def xtf_runner_exit_status(state):
    """ Convert a xtf-runner exit code to a test result. """
    return { 0: "SUCCESS",
             1: "sys.exit 1",
             2: "sys.exit 2",
             3: "SKIP",
             4: "ERROR",
             5: "FAILURE",
             6: "CRASH",
    }[state]

xtf_rundir = '/usr/libexec/xtf'

class XTFMinimalTest(OERuntimeTestCase):

    def query_xtf_cases(self, query_item):
        (status, output) = self.target.run(
                            'cd %s; ./xtf-runner --list %s' % \
                            (xtf_rundir, query_item))
        self.assertTrue(status == 0, msg='XTF runner failed')

        populate_case_lines = output.split('\n')
        while '' in populate_case_lines:
            populate_case_lines.remove('')

        return list(map(lambda x: x.lstrip().rstrip(), populate_case_lines))

    def get_xtf_case_list(self):
        xtf_cases = []

        populate = self.tc.td.get('XTF_TEST_CASES_POPULATE')
        skip = self.tc.td.get('XTF_TEST_CASES_SKIP')
        require = self.tc.td.get('XTF_TEST_CASES_REQUIRE')

        if populate is None:
            populate = DEFAULT_POPULATE

        for query_item in populate.split('|'):
            xtf_cases.extend( self.query_xtf_cases(query_item) )

        if skip is not None:
            for skip_item in skip.split(' '):
                while skip_item in xtf_cases:
                    xtf_cases.remove(skip_item)

        if require is not None:
            for require_item in require.split(' '):
                if require_item == '':
                    continue
                if not require_item in xtf_cases:
                    xtf_cases.append(require_item)

        self.logger.info('XTF cases: %s' % str(xtf_cases))
        return xtf_cases

    def run_xtf_case(self, xtf_case_name):
        (status, output) = self.target.run('cd %s; ./xtf-runner %s' % \
                                           (xtf_rundir, xtf_case_name))
        self.assertTrue(status == 0, msg='XTF test %s failed: %s' % \
                        (xtf_case_name, xtf_runner_exit_status(status)))

    @OETimeout(2400)
    @OETestDepends(['ssh.SSHTest.test_ssh'])
    @OEHasPackage(['xtf'])
    @OEHasPackage(['xen-tools'])
    def test_xtf_minimal(self):

        xtf_cases = self.get_xtf_case_list()

        for xtf_case_name in xtf_cases:
            self.logger.debug('Running XTF case: %s' % xtf_case_name)

            self.run_xtf_case(xtf_case_name)
